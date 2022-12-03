package com.js.shop.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.js.common.util.CheckCodeImage;

@WebServlet("/checkCodeImage")
public class CheckCodeImgServlet extends BaseServlet
{
    // 集合中保存所有成语
    private List<String> words = new ArrayList<String>();

    @Override
    public void init() throws ServletException
    {
        // 初始化阶段，读取new_words.txt
        // web工程中读取 文件，必须使用绝对磁盘路径
        String path = getServletContext().getRealPath("/WEB-INF/new_words.txt");
        try
        {

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), Charset.forName("utf8")));
            String line;
            while ((line = reader.readLine()) != null)
            {
                words.add(line);
            }
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取验证码图片
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void getCheckCode(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        //1、获取验证码
        Random random = new Random();// 生成随机数
        int index = random.nextInt(words.size());
        String word = words.get(index);// 获得验证码字符串
        //2、生成验证码图片
        int width = 120; //定义图片的大小
        int height = 30;
        BufferedImage bufferedImage = CheckCodeImage.generateBufferedImage(word, width, height);

        //3、将验证码内容保存session
        req.getSession().setAttribute("checkcode_session", word);
        // 将生成的图片输出到浏览器 ImageIO
        ImageIO.write(bufferedImage, "jpg", resp.getOutputStream());
    }

}
