package cn.crap.service.thirdly;

import java.util.Map;

import cn.crap.enumer.MyError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.crap.dto.thirdly.GitHubAccessToken;
import cn.crap.dto.thirdly.GitHubUser;
import cn.crap.framework.MyException;
import cn.crap.beans.Config;
import cn.crap.utils.HttpPostGet;
import cn.crap.utils.Tools;

@Service
public class OschinaService {
	@Autowired
	private Config config;
	
	   public GitHubAccessToken getAccessToken(String code,String redirect_uri) throws Exception{
			String oschinaAuthUrl = "https://git.oschina.net/oauth/token";

	        Map<String,String> params = Tools.getStrMap("grant_type", "authorization_code", "client_id", config.getOschinaClientID(),
	        		"client_secret", config.getOschinaClientSecret(),"code",code,"redirect_uri",config.getDomain() +"/oschina/login.ignore");
	        
	        String rs = HttpPostGet.post(oschinaAuthUrl, params, Tools.getStrMap("Accept","application/json"));
	        System.out.println(rs);
	        GitHubAccessToken accessToken = JSON.parseObject(rs,GitHubAccessToken.class);
	        if(accessToken == null || accessToken.getAccess_token() == null) {
				throw new MyException(MyError.E000026);
			}
	        return accessToken;
	    }

	    public GitHubUser getUser(String accessToken) throws Exception{
	        String url = "http://git.oschina.net/api/v5/user?access_token="+accessToken;
	        String rs = HttpPostGet.get(url, null, Tools.getStrMap("Accept","application/json"));
	        System.out.println(rs);
	        if(rs.contains("message"))
	        	throw new MyException(MyError.E000026, rs);
	        return JSON.parseObject(rs,GitHubUser.class);
	    }
}
