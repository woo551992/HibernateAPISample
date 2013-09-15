package common;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtil {
	
	private static SessionFactory sessionFactory;
	
	/** Get the SessionFactory which was built by {@link SessionFactoryBuilder} */
	public static SessionFactory getSessionFactory(){
		if (sessionFactory == null)
			throw new RuntimeException("Session Factory was not built, see " + SessionFactoryBuilder.class.getName());
		return sessionFactory;
	}
	
	public static SessionFactoryBuilder createSessionFactoryBuilder(){
		return new SessionFactoryBuilder();
	}
	
	// INNER CLASS
	public static class SessionFactoryBuilder{
		
		/**
		 * The location of hibernate.cfg.xml
		 * Supported location type - String, File, URL
		 */
		private Object hibernate_cfg_xml = "hibernate.cfg.xml";		// default location [Project]/src/hibernate.cfg.xml
		
		/**
		 * Map{@literal<Resource location, null>}, the value is not used. Use HashMap key to avoid duplicate configuration. <br/>
		 * Supported location type - String, File, URL
		 */
		private final Map<Object, Void> resources = new HashMap<Object, Void>();		

		
		
		/**
		 * Set the path for hibernate.cfg.xml. Note that it should contains Property configuration only.
		 * @param hibernate_cfg_xml_internal_path such as '/com/hibernate.cfg.xml', default is 'hibernate.cfg.xml' which located at [project]/src
		 */
		public SessionFactoryBuilder setPropertyConfiguration(String hibernate_cfg_xml_internal_path){
			return this.setPropertyConfiguration((Object) hibernate_cfg_xml_internal_path);
		}
		/** Same as {@link #setPropertyConfiguration(String)} but support external File */
		public SessionFactoryBuilder setPropertyConfiguration(File hibernate_cfg_xml_file){
			return this.setPropertyConfiguration((Object) hibernate_cfg_xml_file);
		}
		/** Same as {@link #setPropertyConfiguration(String)} but support URL */
		public SessionFactoryBuilder setPropertyConfiguration(URL hibernate_cfg_xml_url){
			return this.setPropertyConfiguration((Object) hibernate_cfg_xml_url);
		}
		/** Entry Setter for hibernate.cfg.xml */
		private SessionFactoryBuilder setPropertyConfiguration(Object hibernate_cfg_xml){
			this.hibernate_cfg_xml = hibernate_cfg_xml;
			return this;
		}
		
		
		/**
		 * Add additional XML configuration such as mapping, to buildSessionFactory. Note that duplicate configuration may cause Exception.
		 * @param resource_xml_internal_path The XML resource location, such as '/com/package/hibernate.cfg.mapping.xml'
		 */
		public SessionFactoryBuilder addConfiguration(String resource_xml_internal_path){
			return this.addConfiguration((Object) resource_xml_internal_path);
		}
		/** Same as {@link #addConfiguration(String)} but support external File */
		public SessionFactoryBuilder addConfiguration(File resource_xml_file){
			return this.addConfiguration((Object) resource_xml_file);
		}
		/** Same as {@link #addConfiguration(String)} but support URL */
		public SessionFactoryBuilder addConfiguration(URL resource_xml_url){
			return this.addConfiguration((Object) resource_xml_url);
		}
		/** Entry Putter for resource*/
		private SessionFactoryBuilder addConfiguration(Object resource){
			resources.put(resource, null);
			return this;
		}
		

		/**
		 * Build a SessionFactory, then you can call {@link HibernateUtil #getSessionFactory()} to reuse it.
		 * @see SessionFactoryBuilder#build(Configuration)
		 */
		public SessionFactory build(){
			return this.build(this.configureAll());
		}
		/**
		 * Configure all resources which was parsed to the Builder. Call this when there are additional configuration which the Builder cannot do.
		 * @return The Configuration which has been configure by the Builder
		 * @calledBy {@link #build()}
		 */
		public Configuration configureAll(){
			final Configuration configuration = new Configuration();
			
			// Configure Property XML. Only one hibernate.cfg.xml is used.
			this.doConfigureSupportedType(configuration, hibernate_cfg_xml);

			// Configure additional XML resources. All resources are used.
			for (Object resource : resources.keySet())
				this.doConfigureSupportedType(configuration, resource);
			
			return configuration;
		}
		/** Call Configure.configure with auto casting supported type */
		private void doConfigureSupportedType(Configuration configuration, Object resource){
			if (resource instanceof String)				// String
				configuration.configure((String) resource);
			else if (resource instanceof File)			// File
				configuration.configure((File) resource);
			else if (resource instanceof URL)			// URL
				configuration.configure((URL) resource);
		}
		
		/** 
		 * Build a SessionFactory by your own configuration 
		 * @see SessionFactoryBuilder#build()
		 */
		public SessionFactory build(Configuration configuration){
			sessionFactory = configuration.buildSessionFactory(new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry());
			return sessionFactory;
		}
	}
	
}
