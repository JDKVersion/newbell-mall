package ltd.newbee.mall.controller.admin;

import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.service.NewBeeMallGoodsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/find")
public class SearchFromJD {
    @Autowired
    NewBeeMallGoodsService newBeeMallGoodsService;
    @RequestMapping("/fromJD")
    public List<NewBeeMallGoods> parseJD(@RequestParam String keyword) throws IOException {
        //获得请求 eg:https://search.jd.com/Search?keyword=java
        String url = "https://search.jd.com/Search?keyword=" + keyword;
        //中文？ 中文在url后面拼上&enc=utf-8
        url = url + "&enc=utf-8";
        System.out.println(url);
        //解析网页。(Jsoup返回Document就是浏览器Document对象)
        Document document = Jsoup.parse(new URL(url), 30000);
        Element element = document.getElementById("J_goodsList");
        //分析京东网页可得 id为"J_goodsList"的div的ul的li 数据在li里

        //获取所有的li元素, 那才是真的数据
        Elements elements = element.getElementsByTag("li");
        // 获取元素中的内容, 这里的el 就是每一个li标签了!
        // 封装个对象 来存储爬的数据
        ArrayList<NewBeeMallGoods> goodsList = new ArrayList<>();
        for (Element el : elements) {
            //获取第一个图片 src属性   图片呢   为什么获取不到呢
            //原来是 source-data-lazy-img   性能
            String img = el.getElementsByTag("img").attr("data-lazy-img");
           // String img =  el.getElementsByClass("p-img").eq(1).tagName("img").tagName("src").toString();
            String price = el.getElementsByClass("p-price").eq(0).text().substring(1);
            String[] pri = price.split(" ");

            String title = el.getElementsByClass("p-name").eq(0).text();
            String shop = el.getElementsByClass("p-shop").eq(0).text();
            NewBeeMallGoods product = new NewBeeMallGoods();
            String [] name = title.split(" ");
            product.setGoodsName(name[0]);
            product.setGoodsIntro(name[0]);
            product.setTag("1");
            product.setStockNum(100);
            product.setGoodsSellStatus((byte) 0);

            product.setGoodsCategoryId(107L);
            product.setGoodsCoverImg(img);
            product.setOriginalPrice((int) Math.round(Double.parseDouble(pri[0])));
            product.setSellingPrice((int) Math.round(Double.parseDouble(pri[0])));
            product.setGoodsDetailContent(title);
            goodsList.add(product);
        }
        for (int i = 0; i < goodsList.size() ; i++) {
            newBeeMallGoodsService.saveNewBeeMallGoods(goodsList.get(i));
        }
        return goodsList;
    }
}
