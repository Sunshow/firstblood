package web.hibernate.usertype;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import com.lehecai.core.IntegerBeanLabel;

public class IntegerBeanLabelUserType implements UserType, ParameterizedType {

	private static final int[] SQL_TYPES = new int[]{Types.INTEGER};
    @SuppressWarnings("unchecked")
	private Class targetClass;

    public void setParameterValues(Properties parameters) {
        String targetClassName = parameters.getProperty("targetClass");
        try {
            targetClass = Class.forName(targetClassName);
        } catch (ClassNotFoundException e) {
            throw new HibernateException("Class " + targetClassName + " not found ", e);
        }
    }

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    @SuppressWarnings("unchecked")
	public Class returnedClass() {
        return targetClass;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return (x == y);
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        int value = rs.getInt(names[0]);
        return rs.wasNull() ? null : IntegerBeanLabel.get(targetClass.getName(),value);
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.INTEGER);
        } else {
            st.setInt(index, ((IntegerBeanLabel)value).getValue());
        }
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
