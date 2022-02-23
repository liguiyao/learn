package com.imooc.mall.controller;

import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.request.UpdateProductReq;
import com.imooc.mall.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Controller
public class ProductAdminController {
    @Autowired
    ProductService productService;

    @ApiOperation("后台添加商品")
    @PostMapping("/admin/product/add")
    @ResponseBody
    public ApiRestResponse add(@Valid @RequestBody AddProductReq addProductReq) {
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }
    @ApiOperation("图片上传")
    @PostMapping("/admin/upload/file")
    @ResponseBody
    public   ApiRestResponse upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        String suffixName = filename.substring(filename.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffixName;
        File fileDriectory = new File(Constant.FILE_UPLOAD_DIR);
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        if (!fileDriectory.exists()) {
            if (!fileDriectory.mkdir()) {
                throw new ImoocMallException(ImoocMallExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return ApiRestResponse.success(getHost(new URI(httpServletRequest.getRequestURI()+""))+"/images/"+newFileName);
        } catch (URISyntaxException e) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.UPLOAD_FAILED);
        }
    }

    private URI getHost(URI uri) {
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }
    @ApiOperation("商品更新")
    @PostMapping("/admin/product/update")
    @ResponseBody
    public ApiRestResponse updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq) {
        productService.update(updateProductReq);
        return ApiRestResponse.success();
    }
    @ApiOperation("后台删除商品")
    @PostMapping("/admin/product/delete")
    @ResponseBody
    public ApiRestResponse delete(@RequestParam Integer id) {
        productService.delete(id);
        return ApiRestResponse.success();
    }
    @ApiOperation("后台批量上下架")
    @PostMapping("/admin/product/batchUpdateSellStatus")
    @ResponseBody
    public ApiRestResponse batchUpdateSellStatus(@RequestParam Integer[] ids, @RequestParam Integer sellStatus) {
        productService.batchUpdateSellStatus(ids, sellStatus);
        return ApiRestResponse.success();
    }
    @ApiOperation("后台商品列表")
    @PostMapping("/admin/product/list")
    @ResponseBody
    public ApiRestResponse list(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        PageInfo pageInfo = productService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }


}
