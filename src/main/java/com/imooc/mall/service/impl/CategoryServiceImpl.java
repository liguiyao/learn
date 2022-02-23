package com.imooc.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.dao.CategoryMapper;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.request.AddCategoryReq;
import com.imooc.mall.model.vo.CategoryVO;
import com.imooc.mall.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public void add(AddCategoryReq addCategoryReq) {
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryReq, category);
        Category categoryOld = categoryMapper.selectByName(addCategoryReq.getName());
        if (categoryOld != null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        int count = categoryMapper.insertSelective(category);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
        }
    }
    @Override
    public void update(Category updateCategory) {
        if (updateCategory != null) {
            Category category = categoryMapper.selectByName(updateCategory.getName());
            if (category != null && !updateCategory.getId().equals(category.getId())) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NAME_EXISTED);
            }
        }
        int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }

    }
    @Override
    public void delete(Integer id){
        Category category = categoryMapper.selectByPrimaryKey(id);
        if (category == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NOT_CATEGORY);
        }
        int count = categoryMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }
    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "type,order_num");
        List<Category> categories = categoryMapper.selectList();
        PageInfo pageInfo = new PageInfo(categories);
        return pageInfo;
    }
    @Override
    @Cacheable(value = "listForCustomer")
    public List<CategoryVO> listForCustomer(Integer parentId){
        ArrayList<CategoryVO> categoryVOList = new ArrayList<>();
        recursivelyFindCategoried(categoryVOList, parentId);
        return categoryVOList;
    }

    private void recursivelyFindCategoried(List<CategoryVO> categoryVOlist,Integer parentId){
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        if (!CollectionUtils.isEmpty(categoryList)) {
            for (Category category : categoryList) {
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                categoryVOlist.add(categoryVO);
                recursivelyFindCategoried(categoryVO.getChildCategory(),categoryVO.getId());
            }
        }
    }


}
