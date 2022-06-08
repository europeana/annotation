package eu.europeana.annotation;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

public class SocksProxyConfig extends Authenticator {

	private ProxyAuthenticator auth;
	private String host;
	private String port;
	private String user;
	private String password;

	/**
	 * Create a new SocksProxy setup. To add settings to the system environment
	 * you need to call the init method
	 * 
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 */
	protected SocksProxyConfig(String host, String port, String user, String password) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
	}

	protected void configureSocksProxy(URI socksProxyUrl){
		this.host = socksProxyUrl.getHost();
		
		if(socksProxyUrl.getPort() > 0)
			this.port = String.valueOf(socksProxyUrl.getPort());
		
		if(socksProxyUrl.getUserInfo() != null){
			//userInfo looks like <user>:<password>
			String[] usrInfo = socksProxyUrl.getUserInfo().split(":", 2);
			this.user = usrInfo[0];
			//if password available
			if(usrInfo.length > 1)
				this.password = usrInfo[1];
				
		}	
	}
		
	public SocksProxyConfig(String socksProxyUrl) throws URISyntaxException{
		if(!StringUtils.isEmpty(socksProxyUrl)){
			configureSocksProxy(new URI(socksProxyUrl));
		}
		//else reset socks proxy?
	}
	
	
	/**
	 * Add the proxy configuration to the system environment. Note that this has
	 * to be done before any other connections are setup
	 */
	public void init() {
		System.setProperty("socksProxyHost", this.host);
		System.setProperty("socksProxyPort", this.port);
		// Since Zookeeper uses Java NIO by default and that does't support
		// Socks proxy, we import a newer Zookeeper
		// version that has a Netty-based ClientCnxnSocket class. The line below
		// tells Zookeeper to use that one instead
		//TODO: this doesn't belong here ... move it to proper place
		//System.setProperty("zookeeper.clientCnxnSocket", "org.apache.zookeeper.ClientCnxnSocketNetty");

		if (StringUtils.isNotEmpty(this.user)) {
			System.setProperty("java.net.socks.username", this.user);
			System.setProperty("java.net.socks.password", this.password);
			auth = new ProxyAuthenticator(this.user, this.password);
			Authenticator.setDefault(auth);
		}
	}

	/**
	 *
	 * @return authentication details encoded in base64
	 */
	public String getEncodedAuth() {
		return java.util.Base64.getEncoder().encodeToString((auth.user + ":" + auth.password).getBytes());
	}

	/**
	 * @return authentication details
	 */
	public ProxyAuthenticator getAuth() {
		return auth;
	}

	private static class ProxyAuthenticator extends Authenticator {

		private String user;
		private String password;

		public ProxyAuthenticator(String user, String password) {
			this.user = user;
			this.password = password;
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user, password.toCharArray());
		}
	}
}
