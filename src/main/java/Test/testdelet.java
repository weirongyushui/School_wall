package Test;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.web.mapper.UserMapper;

import java.io.IOException;
import java.io.InputStream;

public class testdelet {
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
            
            // 测试删除id=2的用户
            Long userId = 5L; // 要删除的用户id
            int result = userMapper.deleteById(userId);
            
            // 提交事务
            sqlSession.commit();
            
            if (result > 0) {
                System.out.println("删除成功！");
                System.out.println("成功删除id为" + userId + "的用户");
            } else {
                System.out.println("删除失败！");
                System.out.println("未找到id为" + userId + "的用户");
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
