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
		
		/** The location of hibernate.cfg.xml */
		private String 	hibernate_cfg_xml_internal_path 	= "hibernate.cfg.xml";
		/** Same as {@link #hibernate_cfg_xml_internal_path} but support external File  */
		private File 	hibernate_cfg_xml_file 				= null;
		/** Same as {@link #hibernate_cfg_xml_internal_path} but support URL */
		private URL 	hibernate_cfg_xml_url 				= null;
		
		
		/** Map{@literal<Resource location, null>}, the value is not used. Avoid duplicate configuration. */
		private final Map<String, Object> 	resources_internal_path 	= new HashMap<String, Object>();
		/** Same as {@link #resources_internal_path} but support external File */
		private final Map<File, Object>		resources_file 				= new HashMap<File, Object>();
		/** Same as {@link #resources_internal_path} but support URL */
		private final Map<URL, Object> 		resources_url 				= new HashMap<URL, Object>();
		

		/**
		 * Set the path for hibernate.cfg.xml. Note that it should contains Property configuration only.
		 * @param hibernate_cfg_xml_internal_path such as '/com/hibernate.cfg.xml', default is 'hibernate.cfg.xml' which located at [project]/src
		 */
		public SessionFactoryBuilder setPropertyConfiguration(String hibernate_cfg_xml_internal_path){
			this.hibernate_cfg_xml_internal_path = hibernate_cfg_xml_internal_path;
			return this;
		}
		/** Same as {@link #setPropertyConfiguration(String)} but support external File */
		public SessionFactoryBuilder setPropertyConfiguration(File hibernate_cfg_xml_file){
			this.hibernate_cfg_xml_file = hibernate_cfg_xml_file;
			return this;
		}
		/** Same as {@link #setPropertyConfiguration(String)} but support URL */
		public SessionFactoryBuilder setPropertyConfiguration(URL hibernate_cfg_xml_url){
			this.hibernate_cfg_xml_url = hibernate_cfg_xml_url;
			return this;
		}
		
		
		/**
		 * Add additional XML configuration such as mapping, to buildSessionFactory. Note that duplicate configuration may cause Exception.
		 * @param resource_xml_internal_path The XML resource location, such as '/com/package/hibernate.cfg.mapping.xml'
		 */
		public SessionFactoryBuilder addConfiguration(String resource_xml_internal_path){
			resources_internal_path.put(resource_xml_internal_path, null);
			return this;
		}
		/** Same as {@link #addConfiguration(String)} but support external File */
		public SessionFactoryBuilder addConfiguration(File resource_xml_file){
			resources_file.put(resource_xml_file, null);
			return this;
		}
		/** Same as {@link #addConfiguration(String)} but support URL */
		public SessionFactoryBuilder addConfiguration(URL resource_xml_url){
			resources_url.put(resource_xml_url, null);
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
			
			// Only one hibernate.cfg.xml is used.
			if (hibernate_cfg_xml_file != null)
				configuration.configure(hibernate_cfg_xml_file);			// File
			else if (hibernate_cfg_xml_url != null)
				configuration.configure(hibernate_cfg_xml_url);				// URL
			else	// default
				configuration.configure(hibernate_cfg_xml_internal_path);	// String

			// All resources are used.
			for (File resource : resources_file.keySet())
				configuration.configure(resource);							// File
			for (URL resource : resources_url.keySet())
				configuration.configure(resource);							// URL
			for (String resource : resources_internal_path.keySet())
				configuration.configure(resource);							// String
			
			return configuration;
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
