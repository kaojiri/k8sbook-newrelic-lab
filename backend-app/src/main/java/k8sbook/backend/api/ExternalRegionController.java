package k8sbook.backend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.servlet.ModelAndView;

import com.newrelic.api.agent.NewRelic;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Controller
public class ExternalRegionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalRegionController.class);

	@RequestMapping("/external-region")
	public ModelAndView index(ModelAndView mav) throws IOException, InterruptedException {

		// 処理対象のファイル名をCustom Attributesとして送信
		NewRelic.addCustomParameter("custom-attr-test-url-path", "external-region");

		LOGGER.info("===================== external-region Service Start!! ====================");

		try {
			
			//sleep(1000);
			Thread.sleep(1000);

			RestTemplate restTemplate = new RestTemplate();
			String fooResourceUrl = "http://external-app-service.eks-work.svc.cluster.local:8080/region";
			ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);

			mav.addObject("statusCode", response.getStatusCode()); 
			mav.addObject("responseBody", response.getBody()); 

			// 使用するビューを設定
			mav.setViewName("external-region");
			
		} catch (Exception e) {
			LOGGER.info("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("===================== external-region Service Finished!! ====================");
		return mav;
	}

	private void sleep(Integer dur) throws InterruptedException{
		Thread.sleep(dur);
	}
}