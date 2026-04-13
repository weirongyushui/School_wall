package Test;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.web.entity.User;
import org.example.web.mapper.UserMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class testupdate {
    public static void main(String[] args) throws IOException {
        // 加载MyBatis配置文件
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        
        // 创建SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();
        
        try {
            // 获取UserMapper实例
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            
            // 创建User对象并设置id为1，修改其他属性
            User user = new User();
            user.setId(1L); // 设置要修改的用户id
            user.setUser_id("1");
            user.setNickname("修改后的测试用户"); // 修改昵称
            user.setAvatar("updated_avatar.jpg"); // 修改头像
            user.setGender(0); // 修改性别
            user.setMajor("软件工程"); // 修改专业
            user.setGrade("2021级"); // 修改年级
            user.setIntroduction("这是修改后的测试用户"); // 修改简介
            user.setPhone("13900139000"); // 修改电话
            user.setEmail("updated_test@example.com"); // 修改邮箱
            user.setQq("987654321"); // 修改QQ
            user.setWechat("updated_wechat123"); // 修改微信
            user.setAddress("上海市浦东新区"); // 修改地址
            user.setBirthday(new Date());
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());
            
            // 更新用户
            int result = userMapper.update(user);
            
            // 提交事务
            sqlSession.commit();
            
            if (result > 0) {
                System.out.println("更新成功！");
                
                // 查询更新后的用户
                User updatedUser = userMapper.getByuserId(Integer.parseInt(user.getUser_id()));
                
                if (updatedUser != null) {
                    System.out.println("查询成功！");
                    System.out.println("更新后的用户信息：" + updatedUser);
                } else {
                    System.out.println("未找到更新的用户");
                }
            } else {
                System.out.println("更新失败！");
                System.out.println("未找到id为1的用户");
            }
        } catch (Exception e) {
            // 回滚事务
            sqlSession.rollback();
            e.printStackTrace();
        } finally {
            // 关闭SqlSession
            sqlSession.close();
        }
    }
}
