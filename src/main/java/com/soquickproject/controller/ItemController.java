package com.soquickproject.controller;

import com.soquickproject.controller.viewobject.ItemVO;
import com.soquickproject.error.BusinessException;
import com.soquickproject.response.CommonReturnType;
import com.soquickproject.service.ItemService;
import com.soquickproject.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class ItemController extends BaseController{
    @Autowired
    private ItemService itemService;
    //创建商品的controller
    @RequestMapping(value = "soquick/item/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(value = "title") String title,
                                       @RequestParam(value = "description") String description,
                                       @RequestParam(value = "price") BigDecimal price,
                                       @RequestParam(value = "stock") Integer stock,
                                       @RequestParam(value = "imgUrl") String imgUrl) throws BusinessException {
        System.out.println("到这一步了吗");
        System.out.println(title+" "+description+" "+price+" "+stock+" "+imgUrl);
        //封装service请求用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);
        itemModel.setSales(0);
        System.out.println("完成了吗");
        ItemModel itemModelForReturn = itemService.createItem(itemModel);
        System.out.println("创建成功了吗");
        ItemVO itemVO = convertVOFromModel(itemModelForReturn);
        System.out.println("没有问题吧");
        System.out.println(itemVO);
        return CommonReturnType.create(itemVO);

    }
    @RequestMapping(value="soquick/item/get", method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(value = "id")Integer id) {
        System.out.println("到这一步了吗  "+id);
        ItemModel itemModel = itemService.getItemById(id);
        System.out.println("查询是否成功");
        ItemVO itemVO = convertVOFromModel(itemModel);
        Map<String,Object> map=new HashMap<>();
        map.put("good",itemVO);
        return CommonReturnType.create(map);
    }
    //商品列表页面浏览
    @GetMapping("soquick/item/list")
    @ResponseBody
    public CommonReturnType listItem() {
        List<ItemModel> itemModelList = itemService.listItem();
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = this.convertVOFromModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        Map<String,Object> map=new HashMap<>();
        map.put("goodlist",itemVOList);
        return CommonReturnType.create(map);
    }
    private ItemVO convertVOFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        if (itemModel.getPromoModel() != null) {
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().
                    toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        } else {
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }



}
