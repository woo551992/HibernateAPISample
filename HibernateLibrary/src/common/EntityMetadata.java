package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;


public class EntityMetadata{
	private Identifier identifier;
	private List<Property> properties;
	
	public EntityMetadata(Object entity, SessionFactory sessionFactory) {
		this(entity, sessionFactory.getClassMetadata(entity.getClass()));
	}
	public EntityMetadata(Object entity, ClassMetadata classMetadata) {
		if (classMetadata == null)
			throw new RuntimeException("classMetadata is null");
		this.identifier = new Identifier(entity, classMetadata);
		this.properties = Property.load(entity, classMetadata);
	}

	public Identifier getIdentifier() {
		return identifier;
	}
	public List<Property> getProperties() {
		return properties;
	}
	
	public boolean hasSinglePrimaryKey(){
		return !(identifier.getType().isComponentType() || 
				identifier.getType().isAnyType() || 
				identifier.getType().isAssociationType() || 
				identifier.getType().isCollectionType());
	}
	
	public boolean hasCompositePrimaryKey(){
		return identifier.getType().isComponentType();
	}
	
//	INNER CLASS		\\
	public static class Identifier{
		public Identifier(Object entity, SessionFactory sessionFactory) {
			this(entity, sessionFactory.getClassMetadata(entity.getClass()));
		}
		public Identifier(Object entity, ClassMetadata classMetadata){
			this.identifier = classMetadata.getIdentifier(entity);
			this.propertyName = classMetadata.getIdentifierPropertyName();
			this.type = classMetadata.getIdentifierType();
		}
		private Serializable identifier;
		private String propertyName;
		private org.hibernate.type.Type type;
		public Serializable getValue() {
			return identifier;
		}
		public String getPropertyName() {
			return propertyName;
		}
		public org.hibernate.type.Type getType() {
			return type;
		}
	}
	
	public static class Property{
		public static List<Property> load(Object entity, SessionFactory sessionFactory){
			return load(entity, sessionFactory.getClassMetadata(entity.getClass()));
		}
		public static List<Property> load(Object entity, ClassMetadata classMetadata){
			List<Property> properties = new ArrayList<Property>();
			
			String[] names = classMetadata.getPropertyNames();
			org.hibernate.type.Type[] types = classMetadata.getPropertyTypes();
			Object[] values = classMetadata.getPropertyValues(entity);
			boolean[] nullabilities = classMetadata.getPropertyNullability();
			boolean[] lazinesses = classMetadata.getPropertyLaziness();
			
			for (int i = 0; i < names.length; i++)
				properties.add(new Property(names[i], types[i], values[i], nullabilities[i], lazinesses[i]));
			return properties;
		}
		public Property(String name, org.hibernate.type.Type type, Object value, boolean nullability, boolean laziness){
			this.name = name;
			this.type = type;
			this.value = value;
			this.nullability = nullability;
			this.laziness = laziness;
		}
		private String name;
		private org.hibernate.type.Type type;
		private Object value;
		private boolean nullability;
		private boolean laziness;
		public String getName() {
			return name;
		}
		public org.hibernate.type.Type getType() {
			return type;
		}
		public Object getValue() {
			return value;
		}
		public boolean isNullability() {
			return nullability;
		}
		public boolean isLaziness() {
			return laziness;
		}
	}
}
