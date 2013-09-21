package common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * @definition
 * Controller is the API which provide either internal or external database access. <br/>
 * You should implement controllers instead of writing code at Servlet. <br/>
 * It is recommended to extends this class for API implementation therefore we can add functions in this class for more convenient. <br/>
 * @codeSample
 * <PRE>
 * {@code 
 * public Account getAccount(String username){
 * 	return (Account) getSession().get(Account.class, username);
 * }
 * </PRE>
 */
public class BaseController {
	
	/**
	 * Every Controller need a Session to access database, 
	 * we don't {@link org.hibernate.SessionFactory #openSession()} every time in API methods
	 * so Session can be reused or transaction can be made by the user-class.
	 */
	private final Session session;

	public BaseController(Session session){
		this.session = session;
	}

	public Session getSession() {
		return session;
	}
	
	
	
	
//		Utilize		\\
	
//region search
	/** Construct a new instance for {@link Searcher} */
	protected Searcher createSearcher(SearchMode searchMode){
		return new Searcher(searchMode);
	}
	/**
	 * Provide basic search filtering for convenient.
	 * @codeSample
	 * <PRE>
	 * {@code
	 * Criteria criteria = createSearcher(SearchMode.BY_NON_NULL_FIELDS)
	 * 						.exclude("password")	// do not search for password property
	 * 						.createCriteria(Account.class, accountCriteria);
	 * // you can do additional filtering by hibernate Criteria
	 * }
	 * </PRE>
	 *
	 */
	protected class Searcher{
		private SearchMode searchMode;
		/** Collection of exclusion properties' name */
		private List<String> exclusion = new ArrayList<String>();

		public Searcher(SearchMode searchMode){
			this.searchMode = searchMode;
		}
		
		/**
		 * Create hibernate Criteria that has been added Restrictions based on the Search Mode, see {@link SearchMode}
		 * @param searchMode
		 * @param searchEntity the entity you want to search
		 * @param entityCriteria the criteria for generating Restrictions, null is allowed (select all)
		 * @return hibernate Criteria that you can add additional Restrictions
		 */
		public <T> Criteria createCriteria(Class<T> searchEntity, T entityCriteria){
			final Criteria criteria = getSession().createCriteria(searchEntity);
			if (entityCriteria == null) return criteria;	// no need to do Restrictions for null criteria

			final EntityMetadata entity = new EntityMetadata(entityCriteria, getSession().getSessionFactory());
			
			// handle criteria for properties
			switch (searchMode) {
			case BY_NON_NULL_FIELDS:
				for (EntityMetadata.Property property : entity.getProperties()) {
					String name = property.getName();
					Object value = property.getValue();
					
					// handle primitive default value, set null to exclude from criteria
					if (!property.isNullability()) {
						value = isPrimitiveDefaultValue(value) ? null : value;
						if (value instanceof Boolean) value = null;					// exclude boolean fields
					}
					
					if (value != null &&											// include fields which is not null
						!property.getType().isAssociationType() &&					// exclude fields which generate by foreign key joining
						!isExcluded(name)											// exclude specified fields
						) {
						criteria.add(Restrictions.eq(name, value));
					}
				}
				
				break;
			} 
			
			// handle criteria for primary key(s)
			switch (searchMode) {
			case BY_NON_NULL_FIELDS:
				// info: every identifier is null when entity is created.
				if (entity.getIdentifier().getValue() != null) {	// such as user.getUsername()!=null, or account.getId()!=null <- getId() is composite primary keys 
					if (entity.hasSinglePrimaryKey()) {
						criteria.add(Restrictions.eq(entity.getIdentifier().getPropertyName(), entity.getIdentifier().getValue()));
					} else if (entity.hasCompositePrimaryKey()) {
						// use reflection to access id properties
						for (Field field : entity.getIdentifier().getValue().getClass().getDeclaredFields()) {
							String name = entity.getIdentifier().getPropertyName() + "." + field.getName();
							Object value = null;
							try {
								field.setAccessible(true);
								value = field.get(entity.getIdentifier().getValue());
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}

							// handle primitive default value, set null to exclude from criteria
							if (true) {
								value = isPrimitiveDefaultValue(value) ? null : value;
								if (value instanceof Boolean) value = null;			// exclude boolean fields
							}
							
							if (value != null &&									// include fields which is not null
								!isExcluded(name)									// exclude specified fields
								){ 
								criteria.add(Restrictions.eq(name, value));
							}
						}
					} else {
						throw new RuntimeException("Unsupport criteria identifier type");
					}
				}
				
				break;
			}
			
			return criteria;
		}
		
		/** Check whether the property has added by {@link #exclude(String)} */
		public boolean isExcluded(String propertyName){
			return exclusion.contains(propertyName);
		}
		
		/**
		 * Exclude search for a property.
		 * @param propertyName such as 'username' for single primary key, 'id.username' for composite primary keys
		 * @return
		 */
		public Searcher exclude(String propertyName){
			exclusion.add(propertyName); return this;
		}
		
	}
	
	protected enum SearchMode{
		/**
		 * Search by Entity with fields that is not null.<br/>
		 * exclude primitive type with default value <br/>
		 * exclude boolean type <br/>
		 */
		BY_NON_NULL_FIELDS
	}
	
	/**
	 * Check whether a primitive variable is default value or not <br/>
	 * Support: char, byte, short, int, long, float, double, boolean
	 */
// TODO: this is not a BaseController function, please relocate it.
	protected boolean isPrimitiveDefaultValue(Object value){
		if (value instanceof Character)
			return ((Character) value).charValue() == Character.UNASSIGNED;
		else if (value instanceof Byte)
			return ((Byte) value).byteValue() == 0;
		else if (value instanceof Short)
			return ((Short) value).shortValue() == 0;
		else if (value instanceof Integer)
			return ((Integer) value).intValue() == 0;
		else if (value instanceof Long)
			return ((Long) value).longValue() == 0L;
		else if (value instanceof Float)
			return ((Float) value).floatValue() == 0.0f;
		else if (value instanceof Double)
			return ((Double) value).doubleValue() == 0.0d;
		else if (value instanceof Boolean)
			return ((Boolean) value).booleanValue() == false;
		
		return false;
	}
	
//endregion
	
//region ignore_type_safety
	
	/**
	 * List all records for a entity, the Session {@link #getSession()} is used.
	 */
	protected <T> List<T> list(Class<T> listEntity) {
		return list(getSession().createCriteria(listEntity));
	}
	/**
	 * Call this method instead of {@link Criteria #list()} to ignore type safety warning
	 * @codeSample
	 * List<Account> account = list(getSession().createCriteria(Account.class));
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> list(Criteria listCriteria) {
		return listCriteria.list();
	}
	
	/**
	 * Same as {@link #list(Query)} with a basic HQL statement, the Session {@link #getSession()} is used.
	 * @codeSample
	 * List<Account> account = list("from Account");
	 */
	protected <T> List<T> list(String listQuery) {
		return list(getSession().createQuery(listQuery));
	}
	/**
	 * Call this method instead of {@link Query #list()} to ignore type safety warning
	 * @codeSample 
	 * List<Account> account = list(getSession().createQuery("from Account"));
	 */
	@SuppressWarnings("unchecked")
	protected static <T> List<T> list(Query listQuery){
		return listQuery.list();
	}
//endregion

}
