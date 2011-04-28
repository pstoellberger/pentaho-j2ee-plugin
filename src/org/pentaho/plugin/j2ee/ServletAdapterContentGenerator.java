package org.pentaho.plugin.j2ee;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.enunciate.modules.jersey.EnunciateJerseyServletContainer;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.services.solution.BaseContentGenerator;
import org.pentaho.platform.plugin.services.pluginmgr.PluginClassLoader;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

@SuppressWarnings("serial")
public class ServletAdapterContentGenerator extends BaseContentGenerator {
  
  private static final Log logger = LogFactory.getLog(ServletAdapterContentGenerator.class);
  private IPluginManager pm = PentahoSystem.get(IPluginManager.class);
  private static ConfigurableApplicationContext appContext;
  private static EnunciateJerseyServletContainer servlet;
  

  public ServletAdapterContentGenerator() throws ServletException {
    if(appContext == null) {
      appContext = getSpringBeanFactory();
      servlet = (EnunciateJerseyServletContainer) appContext.getBean("enunciatePluginServlet");
      servlet.init();
    }
  }
  
  public static ConfigurableApplicationContext getAppContext() {
	return appContext;
  }

  @SuppressWarnings("nls")
  @Override
  public void createContent() throws Exception {
    HttpServletRequest request = (HttpServletRequest)this.parameterProviders.get("path").getParameter("httprequest");
    HttpServletResponse response = (HttpServletResponse)this.parameterProviders.get("path").getParameter("httpresponse");
    servlet.service(request, response);
  }

  @Override
  public Log getLogger() {
    return logger;
  }
  
  private ConfigurableApplicationContext getSpringBeanFactory() {
    final PluginClassLoader loader = (PluginClassLoader)pm.getClassLoader("j2ee");
    File f = new File(loader.getPluginDir(), "plugin.spring.xml"); //$NON-NLS-1$
    if (f.exists()) {
      logger.debug("Found plugin spring file @ " + f.getAbsolutePath()); //$NON-NLS-1$
      ConfigurableApplicationContext context = new FileSystemXmlApplicationContext("file:" + f.getAbsolutePath()) { //$NON-NLS-1$
        @Override
        protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {

          beanDefinitionReader.setBeanClassLoader(loader);
        }

        @Override
        protected void prepareBeanFactory(ConfigurableListableBeanFactory clBeanFactory) {
          super.prepareBeanFactory(clBeanFactory);
          clBeanFactory.setBeanClassLoader(loader);
        }

        /** Critically important to override this and return the desired CL **/
        @Override
        public ClassLoader getClassLoader() {
          return loader;
        }
      };
      return context;
    }
    throw new IllegalStateException("no plugin.spring.xml file found"); //$NON-NLS-1$
  }

}
