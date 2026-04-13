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

public class testInsert {
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
            
            // 创建User对象并设置属性
            User user = new User();
            user.setUser_id("2"); // 使用可以转换为整数的值
            user.setNickname("测试用户");
            user.setAvatar("avatar.jpg");
            user.setGender(1);
            user.setMajor("计算机科学与技术");
            user.setGrade("2022级");
            user.setIntroduction("这是一个测试用户");
            user.setPhone("13800138000");
            user.setEmail("test@example.com");
            user.setQq("123456789");
            user.setWechat("wechat123");
            user.setAddress("北京市朝阳区");
            user.setBirthday(new Date());
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());
            
            // 插入用户
            int result = userMapper.insert(user);
            
            // 提交事务
            sqlSession.commit();
            
            if (result > 0) {
                System.out.println("插入成功！");
                
                // 查询插入的用户
                User insertedUser = userMapper.getByuserId(Integer.parseInt(user.getUser_id()));
                
                if (insertedUser != null) {
                    System.out.println("查询成功！");
                    System.out.println("插入的用户信息：" + insertedUser);
                } else {
                    System.out.println("未找到插入的用户");
                }
            } else {
                System.out.println("插入失败！");
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
