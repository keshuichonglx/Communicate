package com.head.ftp;

import org.apache.commons.net.ftp.*;
import sun.misc.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by user on 17-1-10.
 */
public class Ftp {
    public static void main(String[] args) {
        /**
         * FTP下载单个文件测试
         * ftp://ftp.twaren.net/Unix/Web/apache/ace/apache-ace-2.1.0/apache-ace-2.1.0-src.zip
         * ftp://ftp.twaren.net/Unix/Web/apache/ace/apache-ace-2.1.0/apache-ace-2.1.0-maven.zip
         */

        String strHostName = "127.0.0.1" ;
        String strUser = "myftp" ;
        String strPass = "123" ;
        String strDir = "./" ;
        String strPath1 = "./atom-amd64.deb" ;
        String strPath2 = "./a.log" ;
        String strLocalPath1 = "/home/user/aaa.deb" ;
        String strLocalPath2 = "/home/user/aaa.log" ;

        String strPath3 = "./abcde.log" ;
        String strLocalPath3 = "/home/user/myFtp"  ;

        String strDirTest = "./sssss/" ;

//        String strHostName = "ftp.twaren.net" ;
//        String strUser = null ;
//        String strPass = null ;
//        String strDir = "./Unix/Web/apache/ace/apache-ace-2.1.0/" ;

        //列表获取测试
//        FTPFile[] fList = FtpGetFileList(strHostName,strUser,strPass,strDir) ;
//        if ( fList == null )   {
//            System.err.println( "Ftp List Error!" );
//        }
//        else
//        {
//            if ( fList.length == 0 )    {
//                System.out.println( "Empty!" );
//            }
//            else
//            {
//                System.out.println( "File Number:" + fList.length );
//                for ( FTPFile file : fList )    {
//                    System.out.println( file.getName() + " " + file.getSize() );
//                }
//            }
//        }

        //文件下载测试
//        boolean blRet = FtpDownloadFile( strHostName , strUser , strPass , strPath2 , strLocalPath2 ) ;
//        if ( blRet )   {
//            System.out.println( "Download File: " + strPath2 + " ===> OK" );
//        }
//        else    {
//            System.out.println( "Download File: " + strPath2 + " ===> False" );
//        }

        //文件上传测试
//        boolean blRet = FtpUploadFile( strHostName , strUser , strPass , strLocalPath3 , strPath3 ) ;
//        System.out.println( "Upload File: " + strPath3 + " ===> " + ((blRet != false)?"OK":"FALSE") ) ;

        //文件删除测试
//        boolean blRet = FtpDeleteFile( strHostName , strUser , strPass , "./abcde/a.log" ) ;
//        System.out.println( "Delete File: " + strPath3 + " ===> " + ((blRet != false)?"OK":"FALSE") ) ;

        //目录删除测试
//        boolean blRet = FtpRemoveDir( strHostName , strUser , strPass , strDirTest ) ;
    }

    /**
    * 登录FTP服务器
    * */
    public static FTPClient FtpLogin( String strHostName , String strUser , String strPass  )
    {
        FTPClient ftpClient = new FTPClient();
        FTPClientConfig config = new FTPClientConfig();
        ftpClient.configure(config );

        try{
            ftpClient.connect(strHostName);
            System.out.println("链接到服务器: " + strHostName + ".");
            System.out.print( ftpClient.getReplyString() );

            if( !FTPReply.isPositiveCompletion(ftpClient.getReplyCode()) ) {
                ftpClient.disconnect();
                System.err.println("FTP服务器登录失败!");
                return null;
            }
            else System.out.println( "FTP服务器链接成功..." );

            if ( strUser != null && strPass != null )
            {
                if ( !ftpClient.login(strUser, strPass) )  {
                    ftpClient.disconnect();
                    System.err.println("FTP服务器登录失败!");
                    return null;
                }
                else System.out.println( "FTP服务器登录成功..." );
            }
        }
        catch(IOException e) {
            e.printStackTrace();
            ftpClient = null ;
        }

        return ftpClient ;
    }

    /**
    * FTP获取文件列表
    * */
    public static FTPFile[] FtpGetFileList( String strHostName ,
                                            String strUser ,
                                            String strPass ,
                                            String strDirector  )
    {
        FTPClient ftpClient = FtpLogin( strHostName , strUser , strPass  ) ;
        if ( ftpClient == null )
            return null ;

        FTPFile[] files = null ;
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.configure(new FTPClientConfig("com.head.ftp.UnixFTPEntryParser"));
            files = ftpClient.listFiles(strDirector);

            if (ftpClient.logout()) {
                System.out.println("注销成功!");
            } else {
                System.out.println("注销失败!");
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ioe) {
                    // do nothing
                }
            }
        }
        return files;
    }


    public static boolean FtpDownloadFile( String strHostName ,
                                           String strUser ,
                                           String strPass ,
                                           String strPath ,
                                           String strLocalPath )
    {
        FTPClient ftpClient = FtpLogin( strHostName , strUser , strPass  ) ;
        if ( ftpClient == null )
            return false ;

        FileOutputStream fos = null;
        boolean blRet = false ;
        try {
            fos = new FileOutputStream( strLocalPath );
            ftpClient.setBufferSize(1024);
            //设置文件类型（二进制）
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            blRet = ftpClient.retrieveFile(strPath, fos) ;
            if ( blRet )
                System.out.println( "文件下载成功!" );
            else
                System.out.println( "文件下载失败!" );

            if (ftpClient.logout()) {
                System.out.println("注销成功!");
            } else {
                System.out.println("注销失败!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
        return blRet ;
    }

    public static boolean FtpUploadFile( String strHostName ,
                                         String strUser ,
                                         String strPass ,
                                         String strLocalPath ,
                                         String strPath )
    {
        FTPClient ftpClient = FtpLogin( strHostName , strUser , strPass  ) ;
        if ( ftpClient == null )
            return false ;

        InputStream fos = null;
        boolean blRet = false ;
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            fos = new FileInputStream( strLocalPath );
            blRet = ftpClient.storeFile( strPath , fos ) ;

            if ( blRet )
                System.out.println( "文件上传成功!" );
            else
                System.out.println( "文件上传失败!" );

            if (ftpClient.logout())
                System.out.println("注销成功!");
            else
                System.out.println("注销失败!");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
        return blRet ;
    }

    public static boolean FtpDeleteFile( String strHostName ,
                                         String strUser ,
                                         String strPass ,
                                         String strPath )
    {
        FTPClient ftpClient = FtpLogin( strHostName , strUser , strPass  ) ;
        if ( ftpClient == null )
            return false ;

        boolean blRet = false ;
        try {
            blRet = ftpClient.deleteFile( strPath ) ;

            if ( blRet )
                System.out.println( "文件删除成功!" );
            else
                System.out.println( "文件删除失败!" );

            if (ftpClient.logout())
                System.out.println("注销成功!");
            else
                System.out.println("注销失败!");

        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
        return blRet ;
    }

    public static boolean FtpRemoveDir( String strHostName ,
                                        String strUser ,
                                        String strPass ,
                                        String strDir )
    {
        while ( strDir.endsWith("/") )
            strDir = strDir.substring( 0 , strDir.length() - 1 ) ;

        FTPClient ftpClient = FtpLogin( strHostName , strUser , strPass  ) ;
        if ( ftpClient == null )
            return false ;

        boolean blRet = false ;
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.configure(new FTPClientConfig("com.head.ftp.UnixFTPEntryParser"));

            blRet = SubFtpRemoveDir( ftpClient , strDir ) ;

            if ( blRet )
                System.out.println( "目录删除成功!" );
            else
                System.out.println( "目录删除失败!" );

            if (ftpClient.logout())
                System.out.println("注销成功!");
            else
                System.out.println("注销失败!");

        }
        catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
        return blRet ;
    }

    static boolean SubFtpRemoveDir( FTPClient ftpClient , String strDir )
    {
        try {
            FTPFile[] files = ftpClient.listFiles( strDir );
            for (FTPFile f : files) {
                if (f.isDirectory()) {
                    String strFilePath = strDir + "/" + f.getName() ;
                    if ( !SubFtpRemoveDir( ftpClient , strFilePath ))
                        return false ;
                }
                if (f.isFile()) {
                    String strFilePath = strDir + "/" + f.getName() ;
                    System.out.println( "FTP->删除文件: " + strFilePath );
                    if ( !ftpClient.deleteFile( strFilePath ) )
                        return false ;
                }
            }

            System.out.println( "FTP->删除目录: " + strDir );
            if ( !ftpClient.removeDirectory( strDir ) )
                return false ;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
