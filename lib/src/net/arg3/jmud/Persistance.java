/**
 * Project: jMUD
 * Date: 2009-09-10
 * Package: net.arg3.jmud
 * Author: Ryan Jennings <c0der78@gmail.com>
 */
package net.arg3.jmud;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.context.ManagedSessionContext;
import org.hibernate.criterion.Criterion;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ryan Jennings <c0der78@gmail.com>
 * 
 */
public class Persistance {

	static Logger log = LoggerFactory.getLogger(Persistance.class);
	private static SessionFactory sessionFactory = null;
	private static org.hibernate.classic.Session session = null;

	public static int count(Class<?> type) {
		int count = -1;
		Transaction tx = null;
		try {
			Session s = getSession();
			tx = s.beginTransaction();

			Query q = s.createQuery("select count(tmp) from "
					+ type.getSimpleName() + " tmp");

			count = Integer.parseInt(q.uniqueResult().toString());

			tx.commit();
		} catch (HibernateException ex) {
			log.error(ex.getMessage());
			if (tx != null) {
				tx.rollback();
			}
		}
		return count;
	}

	public static void delete(java.lang.Object obj) {
		Transaction tx = null;
		try {
			Session s = getSession();

			tx = s.beginTransaction();

			s.delete(obj);

			tx.commit();
		} catch (HibernateException ex) {
			log.error(ex.getMessage());
			if (tx != null) {
				tx.rollback();
			}
		}
	}

	public static byte[] encryptString(String pwd, String key) {
		Transaction tx = null;
		try {
			Session s = getSession();

			tx = s.beginTransaction();

			Query q = s.createSQLQuery("SELECT AES_ENCRYPT(?, ?)");
			q.setParameter(0, pwd);
			q.setParameter(1, key);

			byte[] res = (byte[]) q.uniqueResult();

			tx.commit();

			return res;

		} catch (HibernateException ex) {
			log.error(ex.getMessage());
			if (tx != null) {
				tx.rollback();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getAll(Class<T> type) {
		Transaction tx = null;
		try {
			Session s = getSession();

			tx = s.beginTransaction();

			Query q = s.createQuery("from " + type.getSimpleName());

			List<T> list = q.list();

			tx.commit();

			return list;
		} catch (HibernateException ex) {
			log.error(ex.getMessage());
			if (tx != null) {
				tx.rollback();
			}
			return new ArrayList<T>();
		}
	}

	public static String[] getColumns(Class<?> type) {

		ClassMetadata md = sessionFactory.getClassMetadata(type);
		return md.getPropertyNames();
	}

	/*
	 * public static Class<?> getColumnType(Class<?> type, String column) {
	 * ClassMetadata md = sessionFactory.getClassMetadata(type);
	 * 
	 * return md.getPropertyType(column).getReturnedClass(); }
	 */
	public static java.lang.Object getColumnValue(java.lang.Object obj,
			String column) {
		ClassMetadata md = sessionFactory.getClassMetadata(obj.getClass());

		return md.getPropertyValue(obj, column, EntityMode.POJO);
	}

	public static String getIdentifier(Class<?> type) {
		ClassMetadata md = sessionFactory.getClassMetadata(type);

		return md.getIdentifierPropertyName();
	}

	public static String getMapKey(Method m) {
		MapKeyColumn mk = m.getAnnotation(MapKeyColumn.class);
		return mk == null ? null : mk.name();
	}

	public static Session getSession() {
		if (sessionFactory == null) {
			try {
				Configuration config = new Configuration();

				sessionFactory = config.configure().buildSessionFactory();

				session = sessionFactory.openSession();

				ManagedSessionContext.bind(session);

			} catch (HibernateException ex) {
				log.error("Initial SessionFactory creation failed." + ex);
				throw new ExceptionInInitializerError(ex);
			}
		}

		return session;
	}

	public static Class<?> getTargetEntity(Method columnType) {
		OneToMany o2m = columnType.getAnnotation(OneToMany.class);
		if (o2m == null) {
			ManyToMany m2m = columnType.getAnnotation(ManyToMany.class);

			if (m2m == null) {
				ManyToOne m2o = columnType.getAnnotation(ManyToOne.class);

				return m2o == null ? null : m2o.targetEntity();
			}
			return m2m == null ? null : m2m.targetEntity();
		}
		return o2m == null ? null : o2m.targetEntity();
	}

	public static boolean isGeneratedValue(Method m) {
		return m.getAnnotation(GeneratedValue.class) != null;
	}

	public static boolean isTextValue(Method m) {
		Column c = m.getAnnotation(Column.class);

		if (c == null) {
			return false;
		}

		return c.columnDefinition().contentEquals("text");
	}

	@SuppressWarnings("unchecked")
	public static <T> T load(Class<T> type, Long id) {
		Transaction tx = null;

		try {
			Session s = getSession();

			tx = s.beginTransaction();

			T data = (T) s.load(type, id);

			tx.commit();

			return data;
		} catch (HibernateException ex) {
			log.error(ex.getMessage());
			if (tx != null)
				tx.rollback();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> query(Class<T> type, Criterion... args) {
		Transaction tx = null;
		try {
			Session s = getSession();
			tx = s.beginTransaction();

			Criteria q = s.createCriteria(type);

			for (Criterion c : args)
				q.add(c);

			List<T> list = q.list();

			tx.commit();

			return list;
		} catch (HibernateException ex) {
			log.error(ex.getMessage());
			if (tx != null) {
				tx.rollback();
			}
			return new ArrayList<T>();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T queryOne(Class<T> type) {
		Transaction tx = null;
		try {
			Session s = getSession();

			tx = s.beginTransaction();

			Criteria q = s.createCriteria(type);

			T value = (T) q.uniqueResult();

			tx.commit();

			return value;
		} catch (HibernateException ex) {
			log.error(ex.getMessage());
			if (tx != null) {
				tx.rollback();
			}
			return null;
		}
	}

	public static void save(java.lang.Object obj) {
		Transaction tx = null;
		try {
			Session s = getSession();

			tx = s.beginTransaction();

			s.saveOrUpdate(obj);

			s.flush();

			s.refresh(obj);

			tx.commit();

		} catch (HibernateException ex) {
			log.error(ex.getMessage());
			if (tx != null) {
				tx.rollback();
			}
		}
	}

	public static void setColumnValue(java.lang.Object obj, String column,
			java.lang.Object value) {

		ClassMetadata md = sessionFactory.getClassMetadata(obj.getClass());

		md.setPropertyValue(obj, column, value, EntityMode.POJO);
	}
}
