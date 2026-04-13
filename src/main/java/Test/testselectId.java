package Test;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.web.entity.User;
import org.example.web.mapper.UserMapper;

import java.io.IOException;
import java.io.InputStream;

public class testselectId {
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
            
            // 测试根据id查询用户
            Integer userId = 1; // 假设要查询id为1的用户
            User user = userMapper.getByuserId(userId);
            
            // 打印查询结果
            if (user != null) {
                System.out.println("查询成功！");
                System.out.println("用户信息：" + user);
            } else {
                System.out.println("未找到id为" + userId + "的用户");
            }
        } finally {
            // 关闭SqlSession
            sqlSession.close();
        }
    }
}
