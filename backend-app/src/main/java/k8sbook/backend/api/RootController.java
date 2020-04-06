package k8sbook.backend.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.servlet.ModelAndView;

import com.newrelic.api.agent.NewRelic;

@Controller
public class RootController {

	@RequestMapping("/")
	public ModelAndView index(ModelAndView mav) {

		// 処理対象のファイル名をCustom Attributesとして送信
		NewRelic.addCustomParameter("custom-attr-test-url-path", "root");

		// ・コントローラーからテンプレートに値を渡す
		// ・変数「msg」に値を設定
		mav.addObject("msg", "externalボタンを押すと外部サービス(test-app)へアクセスします。");

		// 使用するビューを設定
		mav.setViewName("root"); 

		return mav;
	}

}