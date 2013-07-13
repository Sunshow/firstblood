package web.action.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import com.lehecai.core.lottery.cache.DcLottery;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.bean.PageBean;
import com.lehecai.admin.web.service.BIService;
import com.lehecai.admin.web.service.lottery.DcRaceService;
import com.lehecai.admin.web.service.lottery.JclqRaceService;
import com.lehecai.admin.web.service.lottery.JczqRaceService;
import com.lehecai.admin.web.service.lottery.PhaseService;
import com.lehecai.core.api.lottery.DcRace;
import com.lehecai.core.api.lottery.JclqRace;
import com.lehecai.core.api.lottery.JczqRace;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.lottery.PhaseType;
import com.lehecai.core.lottery.cache.JclqLottery;
import com.lehecai.core.lottery.cache.JczqLottery;
import com.lehecai.core.lottery.cache.OnSaleLotteryList;

public class SpecifyPhaseEventAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private PhaseService phaseService;
	private BIService bIService;
	private DcRaceService dcRaceService;
	private JclqRaceService jclqRaceService;
	private JczqRaceService jczqRaceService;

	private Integer lotteryTypeValue;// 彩种列表选中值
	private String phaseStr;// 彩期列表选中值
	private String matchPhaseStr;// 手工输入的彩期值
	private Integer phaseEventTypeValue;// 彩期事件列表选中值
	private String matchStr;// 场次值
	private String matchStartTimeStr;// 开赛时间

	// private List<LotteryType> lotteryTypeList;// 在售彩种列表

	public String handle() {
		return "inputForm";
	}

	public String executeRun() {
		HttpServletResponse response = ServletActionContext.getResponse();

		StringBuffer log = new StringBuffer();
		log.append("目的 : 手工执行彩期守护事件 ; ");

		// 基本参数校验
		if (lotteryTypeValue == null || phaseStr == null || matchPhaseStr == null || phaseEventTypeValue == null || matchStr == null || matchStartTimeStr == null) {
			log.append(String.format("lotteryTypeValue=[%s], ", lotteryTypeValue));
			log.append(String.format("phaseStr=[%s], ", phaseStr));
			log.append(String.format("matchPhaseStr=[%s], ", matchPhaseStr));
			log.append(String.format("phaseEventTypeValue=[%s], ", phaseEventTypeValue));
			log.append(String.format("matchStr=[%s], ", matchStr));
			log.append(String.format("matchStartTimeStr=[%s]", matchStartTimeStr));
			log.append(" --> 基本参数为null!");
			logger.error(log.toString());
			writeRs(response, "执行失败 : 程序错误,请联系技术人员!");
			return null;
		}

		// 校验彩种列表选中值
		LotteryType lotteryType = null;
		{
			lotteryType = LotteryType.getItem(lotteryTypeValue);
			if (lotteryType == null) {
				log.append(String.format(" --> 获取彩种id=[%s]为空!", lotteryTypeValue));
				logger.error(log.toString());
				writeRs(response, String.format("执行失败 : 无此彩种id=[%s]!", lotteryTypeValue));
				return null;
			}
			log.append(String.format("彩种 : %s ; ", lotteryType.getName()));
		}

		// 校验彩期列表选中值
		String phaseString = null;
		{
			log.append(String.format("列表选中彩期 : %s ; ", phaseStr));
			log.append(String.format("手工输入彩期 : %s ; ", matchPhaseStr));

			if ("".equals(phaseStr)) {
				log.append(" --> 列表彩期数据为空串!");
				logger.error(log.toString());
				writeRs(response, "执行失败 : 请正确选择列出的彩期数据!");
				return null;
			}

			if ("".equals(matchPhaseStr)) {
				phaseString = phaseStr;
			} else {
				phaseString = matchPhaseStr;
			}

			// 普通彩种不能执行当前期以及之后的彩期事件,其他彩种放到下面进行判断
			if (isCommonLottery(lotteryType)) {
				String currentPhaseString = null;
				try {
					currentPhaseString = phaseService.getCurrentPhase(PhaseType.getItem(lotteryType)).getPhase();

					if (currentPhaseString == null || "".equals(currentPhaseString)) {
						log.append(" --> 当前期获取失败!");
						logger.error(log.toString());
						writeRs(response, "执行失败 : 未获取到当前期!");
						return null;
					}
				} catch (Exception e) {
					log.append(" --> 当前期获取失败!");
					logger.error(log.toString());
					logger.error(e.getMessage(), e);
					writeRs(response, "执行失败 : 未获取到当前期!");
					return null;
				}
				log.append(String.format("当前期 : %s ; ", currentPhaseString));
				if (phaseString.length() != currentPhaseString.length()) {
					log.append("输入期次与当前期长度不符!");
					logger.error(log.toString());
					writeRs(response, "执行失败 : 输入期次不合法!");
					return null;
				}

				if (currentPhaseString.compareTo(phaseString) <= 0) {
					log.append(" --> 指定期次大于当前期,直接返回.");
					logger.error(log.toString());
					writeRs(response, "执行失败 : 不能执行当前期以及未来期次的彩期事件!");
					return null;
				}
			}
		}

		// 彩期守护事件类型校验
		{
			log.append(String.format("彩期事件类型值 : %s ; ", phaseEventTypeValue));
			// TODO 增加彩期事件时需要修改判断条件
			if (phaseEventTypeValue < 1 || phaseEventTypeValue > 8) {
				log.append(" --> 输入彩期事件类型值错误!");
				logger.error(log.toString());
				writeRs(response, "执行失败 : 请正确选择列出的彩期事件类型!");
				return null;
			}
		}

		String matchString = "";
		// 判断是否是北单,竞彩等彩种
		if (!isCommonLottery(lotteryType)) {
			log.append("属于北单,竞彩等彩种 ");

			// 如果指定了赛程开赛时间,则直接根据其进行场次查询,忽略手工指定场次
			if (!"".equals(matchStartTimeStr)) {
				try {
					log.append(String.format("开赛时间 : %s ; ", matchStartTimeStr));

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date matchStartTime = sdf.parse(matchStartTimeStr);
					Date now = new Date();

					List<String> queryMatchNum = new ArrayList<String>();
					List<String> notEndSaleMatchNum = new ArrayList<String>();
					// 北京单场
					if (isDcLottery(lotteryType)) {
						List<DcRace> races = new ArrayList<DcRace>();

						PageBean pageBean = new PageBean();
						int maxCount = 20;// 每次查询最大条目
						int page = 1;// 第几页
						pageBean.setPageSize(maxCount);
						while (true) {
							pageBean.setPage(page);
							List<DcRace> temp = dcRaceService.findDcRacesByMatchDateAndPhase(matchStartTime, phaseString, pageBean);
							if (temp != null && temp.size() > 0) {
								races.addAll(temp);
								if (temp.size() < maxCount) {
									logger.info("读取到的北京单场赛程列表不足一页,已读取结束");
									break;
								}
							} else {
								logger.info("已读取完所有北京单场赛程列表");
								break;
							}
							page++;// 准备读取下一页
						}

						if (races.isEmpty()) {
							log.append(" --> 根据开赛时间未获取到北京单场赛程信息!");
							logger.error(log.toString());
							writeRs(response, String.format("执行失败 : 未找到[ %s ]开赛的北京单场场次!", matchStartTimeStr));
							return null;
						}

						for (int i = 0; i < races.size(); i++) {
							DcRace race = races.get(i);
							Date endSaleTime = race.getEndSaleTime();
							if (now.before(endSaleTime)) {
								notEndSaleMatchNum.add(String.valueOf(race.getMatchNum()));
								continue;
							}
							queryMatchNum.add(String.valueOf(race.getMatchNum()));
						}
					}

					// 竞彩足球
					else if (JczqLottery.contains(lotteryType)) {
						List<JczqRace> races = new ArrayList<JczqRace>();

						PageBean pageBean = new PageBean();
						int maxCount = 20;// 每次查询最大条目
						int page = 1;// 第几页
						pageBean.setPageSize(maxCount);
						while (true) {
							pageBean.setPage(page);
							List<JczqRace> temp = jczqRaceService.findJczqRacesByMatchDate(matchStartTime, pageBean);
							if (temp != null && temp.size() > 0) {
								races.addAll(temp);
								if (temp.size() < maxCount) {
									logger.info("读取到的北京单场赛程列表不足一页,已读取结束");
									break;
								}
							} else {
								logger.info("已读取完所有北京单场赛程列表");
								break;
							}
							page++;// 准备读取下一页
						}

						if (races.isEmpty()) {
							log.append(" --> 根据开赛时间未获取到竞彩足球赛程信息!");
							logger.error(log.toString());
							writeRs(response, String.format("执行失败 : 未找到[ %s ]开赛的竞彩足球场次!", matchStartTimeStr));
							return null;
						}

						for (int i = 0; i < races.size(); i++) {
							JczqRace race = races.get(i);
							Date endSaleTime = race.getEndSaleTime();
							if (now.before(endSaleTime)) {
								notEndSaleMatchNum.add(race.getMatchNum());
								continue;
							}
							queryMatchNum.add(race.getMatchNum());
						}
					}

					// 竞彩篮球
					else if (JclqLottery.contains(lotteryType)) {
						List<JclqRace> races = new ArrayList<JclqRace>();

						PageBean pageBean = new PageBean();
						int maxCount = 20;// 每次查询最大条目
						int page = 1;// 第几页
						pageBean.setPageSize(maxCount);
						while (true) {
							pageBean.setPage(page);
							List<JclqRace> temp = jclqRaceService.findJclqRacesByMatchDate(matchStartTime, pageBean);
							if (temp != null && temp.size() > 0) {
								races.addAll(temp);
								if (temp.size() < maxCount) {
									logger.info("读取到的北京单场赛程列表不足一页,已读取结束");
									break;
								}
							} else {
								logger.info("已读取完所有北京单场赛程列表");
								break;
							}
							page++;// 准备读取下一页
						}

						if (races.isEmpty()) {
							log.append(" --> 根据开赛时间未获取到竞彩篮球赛程信息!");
							logger.error(log.toString());
							writeRs(response, String.format("执行失败 : 未找到[ %s ]开赛的篮球足球场次!", matchStartTimeStr));
							return null;
						}

						for (int i = 0; i < races.size(); i++) {
							JclqRace race = races.get(i);
							Date endSaleTime = race.getEndSaleTime();
							if (now.before(endSaleTime)) {
								notEndSaleMatchNum.add(race.getMatchNum());
								continue;
							}
							queryMatchNum.add(race.getMatchNum());
						}
					}

					// 如果存在未销售截止的场次,则返回错误
					if (!notEndSaleMatchNum.isEmpty()) {
						log.append(String.format(" --> [%s]场次未销售截止!取消本次操作!", StringUtils.join(notEndSaleMatchNum, ",")));
						logger.error(log.toString());
						writeRs(response, String.format("执行失败 : 请去掉未销售截止场次[%s]!", StringUtils.join(notEndSaleMatchNum, ",")));
						return null;
					}
					matchString = StringUtils.join(queryMatchNum, ",");
				} catch (ParseException e) {
					log.append(" --> 请正确输入比赛投注截止时间!");
					logger.error(log.toString());
					writeRs(response, "执行失败 : 比赛投注截止时间不合法!请正确输入!");
					return null;
				}
			}

			// 未指定开赛时间但指定了比赛场次,则校验其合法性
			else if (!"".equals(matchStr)) {
				log.append(String.format("手工输入待执行场次字符串 : %s ; ", matchStr));

				List<String> specifiedMatchList = new ArrayList<String>();
				String[] matchList = matchStr.split(",");
				for (int i = 0; i < matchList.length; i++) {
					// TODO 暂时只校验为整型
					Pattern pattern = Pattern.compile("[0-9]+");
					Matcher matcher = pattern.matcher(matchList[i]);
					boolean match = matcher.matches();
					if (!match) {
						log.append(" --> 场次号只能为数字!");
						logger.error(log.toString());
						writeRs(response, "执行失败 : 场次号只能为数字!");
						return null;
					}
					specifiedMatchList.add(matchList[i]);
				}

				// 根据指定的场次字符串从数据库获取对应内容
				List<String> queryMatchNumList = new ArrayList<String>();
				List<String> notEndSaleMatchNum = new ArrayList<String>();
				try {
					// 单场
					if (isDcLottery(lotteryType)) {
						List<DcRace> races = new ArrayList<DcRace>();

						PageBean pageBean = new PageBean();
						int maxCount = 20;// 每次查询最大条目
						int page = 1;// 第几页
						pageBean.setPageSize(maxCount);
						while (true) {
							pageBean.setPage(page);
							List<DcRace> temp = dcRaceService.findDcRacesBySpecifiedMatchNumAndPhase(specifiedMatchList, phaseString, pageBean);
							if (temp != null && temp.size() > 0) {
								races.addAll(temp);
								if (temp.size() < maxCount) {
									logger.info("读取到的北京单场赛程列表不足一页,已读取结束");
									break;
								}
							} else {
								logger.info("已读取完所有北京单场赛程列表");
								break;
							}
							page++;// 准备读取下一页
						}

						if (races.isEmpty()) {
							log.append(" --> 根据指定场次查询API返回为空!");
							logger.error(log.toString());
							writeRs(response, "执行失败 : 查询指定场次时API返回为空!");
							return null;
						}

						Date now = new Date();
						for (DcRace race : races) {
							if (now.before(race.getEndSaleTime())) {
								notEndSaleMatchNum.add(String.valueOf(race.getMatchNum()));
								continue;
							}
							queryMatchNumList.add(String.valueOf(race.getMatchNum()));
						}
					}

					// 竞彩篮球
					if (JclqLottery.contains(lotteryType)) {
						List<JclqRace> races = new ArrayList<JclqRace>();

						PageBean pageBean = new PageBean();
						int maxCount = 20;// 每次查询最大条目
						int page = 1;// 第几页
						pageBean.setPageSize(maxCount);
						while (true) {
							pageBean.setPage(page);
							List<JclqRace> temp = jclqRaceService.findJclqRacesBySpecifiedMatchNum(specifiedMatchList, pageBean);
							if (temp != null && temp.size() > 0) {
								races.addAll(temp);
								if (temp.size() < maxCount) {
									logger.info("读取到的北京单场赛程列表不足一页,已读取结束");
									break;
								}
							} else {
								logger.info("已读取完所有北京单场赛程列表");
								break;
							}
							page++;// 准备读取下一页
						}

						if (races.isEmpty()) {
							log.append(" --> 根据指定场次查询API返回为空!");
							logger.error(log.toString());
							writeRs(response, "执行失败 : 查询指定场次时API返回为空!");
							return null;
						}

						Date now = new Date();
						for (JclqRace race : races) {
							if (now.before(race.getEndSaleTime())) {
								notEndSaleMatchNum.add(race.getMatchNum());
								continue;
							}
							queryMatchNumList.add(race.getMatchNum());
						}
					}

					// 竞彩足球
					if (JczqLottery.contains(lotteryType)) {
						List<JczqRace> races = new ArrayList<JczqRace>();

						PageBean pageBean = new PageBean();
						int maxCount = 20;// 每次查询最大条目
						int page = 1;// 第几页
						pageBean.setPageSize(maxCount);
						while (true) {
							pageBean.setPage(page);
							List<JczqRace> temp = jczqRaceService.findJczqRacesBySpecifiedMatchNum(specifiedMatchList, pageBean);
							if (temp != null && temp.size() > 0) {
								races.addAll(temp);
								if (temp.size() < maxCount) {
									logger.info("读取到的北京单场赛程列表不足一页,已读取结束");
									break;
								}
							} else {
								logger.info("已读取完所有北京单场赛程列表");
								break;
							}
							page++;// 准备读取下一页
						}

						if (races == null) {
							log.append(" --> 根据指定场次查询API返回为空!");
							logger.error(log.toString());
							writeRs(response, "执行失败 : 查询指定场次时API返回为空!");
							return null;
						}

						Date now = new Date();
						for (JczqRace race : races) {
							if (now.before(race.getEndSaleTime())) {
								notEndSaleMatchNum.add(race.getMatchNum());
								continue;
							}
							queryMatchNumList.add(race.getMatchNum());
						}
					}
				} catch (Exception e) {
					log.append(" --> 校验手工输入场次号时出现异常!");
					logger.error(log.toString());
					writeRs(response, "执行失败 : 校验手工输入场次号时出现异常!");
					return null;
				}

				// 如果存在未销售截止的场次,则返回错误
				if (!notEndSaleMatchNum.isEmpty()) {
					log.append(String.format(" --> 场次[%s]未销售截止!取消本次操作!", StringUtils.join(notEndSaleMatchNum, ",")));
					logger.error(log.toString());
					writeRs(response, String.format("执行失败 : 请去掉未销售截止场次[%s]!", StringUtils.join(notEndSaleMatchNum, ",")));
					return null;
				}

				// 比较指定场次号与查询到的场次号,将不同的剔出
				List<String> notExistMatchList = new ArrayList<String>();
				for (int i = 0; i < specifiedMatchList.size(); i++) {
					String matchNum = specifiedMatchList.get(i);
					if (!queryMatchNumList.contains(matchNum)) {
						notExistMatchList.add(matchNum);
					}
				}

				if (notExistMatchList.size() > 0) {
					log.append(String.format(" --> 第%s场次不存在!", StringUtils.join(notExistMatchList, ",")));
					logger.error(log.toString());
					writeRs(response, String.format("执行失败 : 第%s场次不存在!", StringUtils.join(notExistMatchList, ",")));
					return null;
				}
				matchString = matchStr;
			}

			// 如果开赛时间与场次字符串均为空
			else {
				log.append(" --> 场次号为空!");
				logger.error(log.toString());
				writeRs(response, "执行失败 : 请输入场次号!");
				return null;
			}
		}

		else {
			log.append("不属于北单,竞彩等彩种 ");

			if (!"".equals(matchStr)) {
				log.append(" --> 不需要填写场次信息!");
				logger.error(log.toString());
				writeRs(response, "执行失败 : 不需要填写场次信息!");
				return null;
			} else if (!"".equals(matchStartTimeStr)) {
				log.append(" --> 不需要填写比赛投注截止时间!");
				logger.error(log.toString());
				writeRs(response, "执行失败 : 不需要填写比赛投注截止时间!");
				return null;
			}
			matchString = "";
		}

		// 执行对engine请求
		Map<String, String> requestMap = new HashMap<String, String>();
		requestMap.put(BIService.PROCESS_CODE, BIService.Specify_PhaseEvent_Process);
		requestMap.put("lotteryTypeValue", String.valueOf(lotteryTypeValue));
		requestMap.put("phaseString", phaseString);
		requestMap.put("phaseEventTypeValue", String.valueOf(phaseEventTypeValue));
		requestMap.put("matchString", matchString);

		Map<String, String> responseMap = bIService.request(requestMap, lotteryType);

		if (responseMap == null || responseMap.size() == 0) {
			log.append(" --> engine返回为空!");
			logger.error(log.toString());
			writeRs(response, "执行失败 : 调用程序返回为空!");
			return null;
		}

		if (responseMap.get(BIService.RESP_CODE).equals("0000")) {
			String info = responseMap.get(BIService.RESP_MESG);
			log.append(" --> 事件执行成功!");
			logger.info(log.toString());
			writeRs(response, "执行成功 : ".concat(info));
		} else {
			String info = responseMap.get(BIService.RESP_MESG);
			log.append(String.format(" --> 事件执行失败 : [%s] ; ", info));
			logger.error(log.toString());
			writeRs(response, "执行失败 : ".concat(info));
		}
		return null;
	}

	/** 是否为普通彩种 */
	private boolean isCommonLottery(LotteryType lotteryType) {
		if (isDcLottery(lotteryType) || JclqLottery.contains(lotteryType) || JczqLottery.contains(lotteryType)) {
			return false;
		} else {
			return true;
		}
	}

	/** 是否为北京单场 */
	private boolean isDcLottery(LotteryType lotteryType) {
		if (DcLottery.getList().contains(lotteryType)) {
			return true;
		} else {
			return false;
		}
	}

	public List<LotteryType> getLotteryTypeList() {
		// 获取在售彩种列表
		return OnSaleLotteryList.get();
	}

	public PhaseService getPhaseService() {
		return phaseService;
	}

	public void setPhaseService(PhaseService phaseService) {
		this.phaseService = phaseService;
	}

	public BIService getbIService() {
		return bIService;
	}

	public void setbIService(BIService bIService) {
		this.bIService = bIService;
	}

	public DcRaceService getDcRaceService() {
		return dcRaceService;
	}

	public void setDcRaceService(DcRaceService dcRaceService) {
		this.dcRaceService = dcRaceService;
	}

	public JclqRaceService getJclqRaceService() {
		return jclqRaceService;
	}

	public void setJclqRaceService(JclqRaceService jclqRaceService) {
		this.jclqRaceService = jclqRaceService;
	}

	public JczqRaceService getJczqRaceService() {
		return jczqRaceService;
	}

	public void setJczqRaceService(JczqRaceService jczqRaceService) {
		this.jczqRaceService = jczqRaceService;
	}

	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}

	public void setPhaseStr(String phaseStr) {
		this.phaseStr = phaseStr;
	}

	public void setMatchPhaseStr(String matchPhaseStr) {
		this.matchPhaseStr = matchPhaseStr;
	}

	public void setPhaseEventTypeValue(Integer phaseEventTypeValue) {
		this.phaseEventTypeValue = phaseEventTypeValue;
	}

	public void setMatchStr(String matchStr) {
		this.matchStr = matchStr;
	}

	public void setMatchStartTimeStr(String matchStartTimeStr) {
		this.matchStartTimeStr = matchStartTimeStr;
	}

}