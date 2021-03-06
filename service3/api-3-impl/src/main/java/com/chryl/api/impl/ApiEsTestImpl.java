package com.chryl.api.impl;

import com.chryl.api.EsApiTest;
import com.chryl.mapper.UserMapper;
import com.chryl.po.ChrUser;
import com.chryl.po.EsChrUser;
import com.chryl.repository.EsProductRepository;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Chr.yl on 2020/6/19.
 *
 * @author Chr.yl
 */
@Service(timeout = 6000)
public class ApiEsTestImpl implements EsApiTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EsProductRepository userEsRepository;
//    @Autowired
//    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public int importAll2Es() {
        List<ChrUser> userList = userMapper.getAllUsers();//从db 查
        List<EsChrUser> esChrUserList = new ArrayList<>();
        for (ChrUser chrUser : userList) {
            EsChrUser esChrUser = new EsChrUser();
            BeanUtils.copyProperties(chrUser, esChrUser);
            //日期格式暂未实现
//            esChrUser.setBirthday(chrUser.getBirthday().toString());
//            esChrUser.setCreateTime(chrUser.getCreateTime().toString());
            esChrUserList.add(esChrUser);
        }
        Iterable<EsChrUser> chrUserIterable = userEsRepository.saveAll(esChrUserList);//入 es,//Validation Failed: 1: no requests added; 为存入es数据为null
        Iterator<EsChrUser> iterator = chrUserIterable.iterator();
        int res = 0;
        while (iterator.hasNext()) {
            res++;
            iterator.next();
        }
        return res;
    }

    @Override
    public List<EsChrUser> findByUserName(String name, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);
        List<EsChrUser> byUsername = userEsRepository.findByUsername(name, pageable);//从es查
        return byUsername;
    }

    @Override
    public void delete() {
        //从es删除
        userEsRepository.deleteAll();
    }

}
