package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.ProductMapper;
import com.imooc.mall.model.pojo.Product;
import com.imooc.mall.model.query.ProductListQuery;
import com.imooc.mall.model.request.AddProductReq;
import com.imooc.mall.model.request.ProductListReq;
import com.imooc.mall.model.request.UpdateProductReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import com.imooc.mall.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryService categoryService;

    @Override
    public void add(AddProductReq addProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(addProductReq, product);
        Product oldProduct = productMapper.findByName(product.getName());
        if (oldProduct != null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.insertSelective(product);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
        }
    }
    @Override
    public void update(UpdateProductReq updateProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        Product oldProduct = productMapper.findByName(product.getName());
        if (oldProduct != null && !oldProduct.getId().equals(product.getId())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_PRODUCT);
        }
        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }
    @Override
    public void delete(Integer id) {
        Product old = productMapper.findById(id);
        if (old == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_PRODUCT);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }
    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        int count = productMapper.batchUpdateSellStatus(ids, sellStatus);
    }
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectListForAdmin();
        PageInfo pageInfo = new PageInfo(products);
        return pageInfo;
    }
    @Override
    public Product detail(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        return product;
    }
    @Override
    public PageInfo list(ProductListReq productListReq) {
        ProductListQuery productListQuery = new ProductListQuery();
        //搜索处理
        if (!StringUtils.isEmpty(productListReq.getKeyword())) {
            String keyword = new StringBuilder().append("%").append(productListReq.getKeyword()).append("%").toString();
            productListQuery.setKeyword(keyword);
        }
        //分类查找处理
        if (productListReq.getCategoryId() != null) {
            List<CategoryVO> categoryVOS = categoryService.listForCustomer(productListReq.getCategoryId());
            ArrayList<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVOS, categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }
        //排序处理
        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(), orderBy);
        }else {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
        }
        //对数据库查询
        List<Product> productList = productMapper.selectList(productListQuery);
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }

    private void getCategoryIds(List<CategoryVO> categoryVOList,ArrayList<Integer> categoryIds){
        for (CategoryVO categoryVO : categoryVOList) {
            if (categoryVO != null) {
                categoryIds.add(categoryVO.getId());
                getCategoryIds(categoryVO.getChildCategory(), categoryIds);
            }
        }
    }
}
