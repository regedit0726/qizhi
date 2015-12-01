package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Administrator on 2015/11/24.
 */
@SuppressWarnings("deprecation")
public class TestUtils
{

    private TestUtils()
    {
    }

    /**
     * 记录一条字符串到指定文件末尾处
     * @param src 待记录字符串
     * @param desFileName 目标文件名
     */
    @Deprecated
    public static void recordInFile(String src, String desFileName)
    {
        recordInFile(new String[] { src }, desFileName, true);
    }

    /**
     * 记录一条字符串到指定文件末尾处
     * @param src 待记录字符串
     * @param desFileName 目标文件名
     */
    @Deprecated
    public static void recordInFile(String src, String desFileName, boolean isAppend)
    {
        recordInFile(new String[] { src }, desFileName, isAppend);
    }

    /**
     * 记录一个字符串数组到指定文件末尾处
     * @param src 待记录字符串
     * @param desFileName 目标文件名
     */
    @Deprecated
    public static void recordInFile(String[] src, String desFileName)
    {
        recordInFile(src, desFileName, true);
    }

    /**
     * 记录一个字符串数组到指定文件末尾处
     * @param src 待记录字符串
     * @param desFileName 目标文件名
     */
    @Deprecated
    public static void recordInFile(String[] src, String desFileName, boolean isAppend)
    {
        File file = new File(desFileName);
        if (file.isDirectory())
        {
            return;
        }
        BufferedWriter bw = null;
        try
        {
            if ((!file.exists()) && (!file.createNewFile()))
            {
                System.out.println("Failed to create new File" + desFileName);
                return;
            }
            bw = new BufferedWriter(new FileWriter(file, isAppend));
            for (String s : src)
            {
                bw.write(s + "\r\n");
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (bw != null)
            {
                try
                {
                    bw.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}