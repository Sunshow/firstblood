package web.action.include.statics.cms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.lehecai.admin.web.action.BaseAction;
import com.lehecai.admin.web.domain.cms.RecommendRace;
import com.lehecai.admin.web.service.cms.RecommendRaceService;
import com.lehecai.core.lottery.LotteryType;
import com.lehecai.core.type.cooperator.Cooperator;
import com.opensymphony.xwork2.Action;

public abstract class AbstractRecommendRaceStaticAction<T> extends BaseAction {

	private static final long serialVersionUID = 6451784479409003632L;

	private RecommendRaceService recommendRaceService;
	
	private Cooperator cooperator;
	
	private Integer lotteryTypeValue;				//彩种编码
	private Integer count = 2;						//查询总记录数
	private boolean orderByRecommendValue = true;	//是否按推荐值排序
    private boolean rotate = false;                 //是否自动使用新赛事补全
    private boolean priority = true;                //是否使用赛程的优先级排序
	
	private String matchName;						//按照赛事名称筛选

	abstract protected T getMatch(RecommendRace recommendRace);

    abstract protected String getMatchName(T match);

    abstract protected String getMatchNum(T match);

    abstract protected int getPriority(T match);
    
    protected void getExtraMatchInfo(JSONArray jsonArray){}

    /**
     * 获取在售赛程列表，如需支持则覆盖实现此方法
     * @return
     */
    protected List<T> getOnsaleMatchList() throws Exception {
        return null;
    }
	
	abstract protected Date getMatchDate(T match);
	
	abstract protected void outputMatchData(T match, RecommendRace recommendRace, JSONObject json);
	
	public String handle () {
		if (cooperator == null) {
			logger.error("无效的合作商");
			super.setErrorMessage("无效的合作商");
			return "failure";
		}
		
		LotteryType lotteryType = lotteryTypeValue == null ? null : LotteryType.getItem(lotteryTypeValue);
		if (lotteryType == null || lotteryType.getValue() == LotteryType.ALL.getValue()) {
			logger.error("未指定彩种编码");
			super.setErrorMessage("未指定彩种编码");
			return "failure";
		}
		
		// 先取出该彩种和合作商下的所有推荐赛程
		List<RecommendRace> allRecommendRaceList = recommendRaceService.findList(cooperator, lotteryType);			//查询所有推荐赛程
		
		List<RecommendRace> recommendRaceList = new ArrayList<RecommendRace>();			//未过期的场次
		List<RecommendRace> expiredRecommendRaceList = new ArrayList<RecommendRace>();	//已过期的场次

		Map<Long, T> matchDetailMap = new HashMap<Long, T>();
        Map<String, T> matchNumKeyDetailMap = new HashMap<String, T>();
		
		Date currentDate = new Date();
		for (RecommendRace recommendRace : allRecommendRaceList) {
			T match = this.getMatch(recommendRace);
			matchDetailMap.put(recommendRace.getId(), match);
            matchNumKeyDetailMap.put(this.getMatchNum(match), match);

			if (match == null) {
				logger.error("获取对阵异常，跳过本场，recommendRaceId={}", recommendRace.getId());
				continue;
			}
			Date matchDate = this.getMatchDate(match);
			
			if (matchDate.getTime() > currentDate.getTime()) {		//比赛日期在当前时间之后，则为未过期
				recommendRaceList.add(recommendRace);				//未过期的
			} else {
				expiredRecommendRaceList.add(recommendRace);		//已过期的
			}
		}
		
		if (orderByRecommendValue) {
			// 如果按照优先级排序
			Collections.sort(recommendRaceList, new Comparator<RecommendRace>() {

				@Override
				public int compare(RecommendRace race1, RecommendRace race2) {
					return race2.getRecommendValue() - race1.getRecommendValue();
				}
				
			});
		}
		
		List<RecommendRace> outputRaceList = null;
		boolean filtered = false;
		if (!StringUtils.isEmpty(matchName)) {
			outputRaceList = new ArrayList<RecommendRace>();
			for (RecommendRace recommendRace : recommendRaceList) {
				if (matchName.equals(recommendRace.getMatchName())) {
					outputRaceList.add(recommendRace);
				} else {
					filtered = true;
				}
			}
		} else {
			outputRaceList = recommendRaceList;
		}

        int needMore = 0;
		if (outputRaceList.size() >= count) {
			outputRaceList = outputRaceList.subList(0, count);
		} else {
            needMore = count - outputRaceList.size();
        }

        // 先输出可推荐的赛事

        JSONArray jsonArray = new JSONArray();

        for (RecommendRace recommendRace : outputRaceList) {
            JSONObject json = new JSONObject();

            json.put("id", recommendRace.getId().toString());
            json.put("match_num", recommendRace.getMatchNum().toString());
            json.put("home_team", recommendRace.getHomeTeam());
            json.put("away_team", recommendRace.getAwayTeam());
            json.put("match_name", recommendRace.getMatchName());

            T match = matchDetailMap.get(recommendRace.getId());
            this.outputMatchData(match, recommendRace, json);

            jsonArray.add(json);
        }

        if (needMore > 0) {
            List<T> outputMatchList = new ArrayList<T>();
            List<RecommendRace> outputMoreRaceList = new ArrayList<RecommendRace>();

            List<T> allOnsaleMatchList = null;

            if (this.isRotate()) {
                // 获取所有已开售的赛事
                try {
                    allOnsaleMatchList = this.getOnsaleMatchList();
                } catch (Exception e) {
                    logger.error("获取可售场次出错", e);
                    super.setErrorMessage("获取可售场次出错");
                    return "failure";
                }
            }

            if (allOnsaleMatchList != null) {
                // 按照优先级排序
                if (this.isPriority()) {
                    // 用Collections.sort的Comparator匿名类无法调用外部方法，自己实现排序
                    int size = allOnsaleMatchList.size();
                    for (int i = 0; i < size; i++) {
                        int maxIndex = i;
                        int minMatchNumIndex = i;
                        for (int j = i + 1; j < size; j++) {
                            if (this.getPriority(allOnsaleMatchList.get(maxIndex)) < this.getPriority(allOnsaleMatchList.get(j))) {
                                maxIndex = j;
                            }
                            if (Long.parseLong(this.getMatchNum(allOnsaleMatchList.get(minMatchNumIndex))) > Long.parseLong(this.getMatchNum(allOnsaleMatchList.get(j)))) {
                                minMatchNumIndex = j;
                            }
                        }

                        // 将最大的索引和i进行交换
                        if (maxIndex != i) {
                            T race = allOnsaleMatchList.get(i);
                            allOnsaleMatchList.set(i, allOnsaleMatchList.get(maxIndex));
                            allOnsaleMatchList.set(maxIndex, race);
                        } else {
                            if (minMatchNumIndex != i) {
                                T race = allOnsaleMatchList.get(i);
                                allOnsaleMatchList.set(i, allOnsaleMatchList.get(minMatchNumIndex));
                                allOnsaleMatchList.set(minMatchNumIndex, race);
                            }
                        }
                    }
                }

                // 按赛事名称过滤补足
                if (!StringUtils.isEmpty(matchName)) {
                    for (T match : allOnsaleMatchList) {
                        if (matchNumKeyDetailMap.containsKey(this.getMatchNum(match))) {
                            // 排除已推荐的场次
                            continue;
                        }
                        if (matchName.equals(this.getMatchName(match))) {
                            outputMatchList.add(match);

                            needMore --;

                            if (needMore == 0) {
                                // 补够了
                                break;
                            }
                        }
                    }
                }

                // 如果还没补够
                if (needMore > 0) {
                    for (T match : allOnsaleMatchList) {
                        if (matchNumKeyDetailMap.containsKey(this.getMatchNum(match))) {
                            // 排除已推荐的场次
                            continue;
                        }
                        if (!outputMatchList.contains(match)) {
                            outputMatchList.add(match);

                            needMore --;

                            if (needMore == 0) {
                                // 补够了
                                break;
                            }
                        }
                    }
                }
            }

            if (needMore > 0) {
                if (filtered) {
                    // 从过滤掉的结果里补全数据
                    for (RecommendRace recommendRace : recommendRaceList) {
                        if (!outputRaceList.contains(recommendRace)) {
                            outputMoreRaceList.add(recommendRace);

                            needMore --;

                            if (needMore == 0) {
                                // 补够了
                                break;
                            }
                        }
                    }
                }
            }

			// 最后还是不够，从已过期的比赛里面取数据补全
			if (needMore > 0) {
				// 从已过期赛事里面选择数据来补全数据
				for (RecommendRace recommendRace : expiredRecommendRaceList) {
                    outputMoreRaceList.add(recommendRace);

                    needMore --;

                    if (needMore == 0) {
                        // 补够了
                        break;
                    }
				}
			}

            for (RecommendRace recommendRace : outputMoreRaceList) {
                JSONObject json = new JSONObject();

                json.put("id", recommendRace.getId().toString());
                json.put("match_num", recommendRace.getMatchNum().toString());
                json.put("home_team", recommendRace.getHomeTeam());
                json.put("away_team", recommendRace.getAwayTeam());
                json.put("match_name", recommendRace.getMatchName());

                T match = matchDetailMap.get(recommendRace.getId());
                this.outputMatchData(match, recommendRace, json);

                jsonArray.add(json);
            }

            for (T match : outputMatchList) {
                JSONObject json = new JSONObject();

                json.put("id", "");
                json.put("match_num", this.getMatchNum(match));
                json.put("match_name", this.getMatchName(match));

                this.outputMatchData(match, null, json);

                jsonArray.add(json);
            }
		}
        //处理额外添加消息
        this.getExtraMatchInfo(jsonArray);
        
		JSONObject rs = new JSONObject();
		rs.put("data", jsonArray);
		
		logger.info(rs.toString());
		
		super.writeRs(ServletActionContext.getResponse(), rs);
		
		return Action.NONE;
	}

	public RecommendRaceService getRecommendRaceService() {
		return recommendRaceService;
	}

	public void setRecommendRaceService(RecommendRaceService recommendRaceService) {
		this.recommendRaceService = recommendRaceService;
	}

	public void setCooperatorId(Integer cooperatorId) {
		if (cooperatorId != null) {
			this.cooperator = Cooperator.getItem(cooperatorId);
		}
	}

	public Integer getLotteryTypeValue() {
		return lotteryTypeValue;
	}

	public void setLotteryTypeValue(Integer lotteryTypeValue) {
		this.lotteryTypeValue = lotteryTypeValue;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public boolean isOrderByRecommendValue() {
		return orderByRecommendValue;
	}

	public void setOrderByRecommendValue(boolean orderByRecommendValue) {
		this.orderByRecommendValue = orderByRecommendValue;
	}

	public String getMatchName() {
		return matchName;
	}

	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}

    public boolean isRotate() {
        return rotate;
    }

    public void setRotate(boolean rotate) {
        this.rotate = rotate;
    }

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }
}
