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
        String strLocalPath3 = "/home/user/myFtp" ;

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
        boolean blRet = FtpUploadFile( strHostName , strUser , strPass , strLocalPath3 , strPath3 ) ;
        System.out.println( "Upload File: " + strPath3 + " ===> " + ((blRet != false)?"OK":"FALSE") ) ;
    }

    /**
    * FTP获取文件列表
    * */
    public static FTPFile[] FtpGetFileList( String strHostName ,
                                            String strUser ,
                                            String strPass ,
                                            String strDirector  )
    {
        FTPClient ftpClient = new FTPClient();
        FTPClientConfig config = new FTPClientConfig();
        ftpClient.configure(config );

        FTPFile[] files = null ;

        try {
            ftpClient.connect(strHostName);
            System.out.println("链接到服务器: " + strHostName + ".");
            System.out.print( ftpClient.getReplyString() );

            if( FTPReply.isPositiveCompletion(ftpClient.getReplyCode()) == false ) {
                ftpClient.disconnect();
                System.err.println("FTP服务器登录失败!");
                return null;
            }
            else System.out.println( "FTP服务器链接成功..." );

            if ( strUser != null && strPass != null )
            {
                if ( ftpClient.login(strUser, strPass) == false )  {
                    ftpClient.disconnect();
                    System.err.println("FTP服务器登录失败!");
                    return null;
                }
                else System.out.println( "FTP服务器登录成功..." );
            }

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
        FTPClient ftpClient = new FTPClient();
        FTPClientConfig config = new FTPClientConfig();
        ftpClient.configure(config );
        FileOutputStream fos = null;
        boolean blRet = false ;
        try {
            ftpClient.connect(strHostName);
            System.out.println("链接到服务器: " + strHostName + ".");
            System.out.print( ftpClient.getReplyString() );

            if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode()) == false ) {
                ftpClient.disconnect();
                System.err.println("FTP服务器登录失败!");
                return false;
            }
            else System.out.println( "FTP服务器链接成功..." );

            if ( strUser != null && strPass != null )
            {
                if ( ftpClient.login(strUser, strPass) == false )  {
                    ftpClient.disconnect();
                    System.err.println("FTP服务器登录失败!");
                    return false;
                }
                else System.out.println( "FTP服务器登录成功..." );
            }

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
        FTPClient ftpClient = new FTPClient();
        FTPClientConfig config = new FTPClientConfig();
        ftpClient.configure(config );
        InputStream fos = null;
        boolean blRet = false ;
        try {
            ftpClient.connect(strHostName);
            System.out.println("链接到服务器: " + strHostName + ".");
            System.out.print( ftpClient.getReplyString() );

            if ( FTPReply.isPositiveCompletion(ftpClient.getReplyCode()) == false ) {
                ftpClient.disconnect();
                System.err.println("FTP服务器登录失败!");
                return false;
            }
            else System.out.println( "FTP服务器链接成功..." );

            if ( strUser != null && strPass != null )
            {
                if ( ftpClient.login(strUser, strPass) == false )  {
                    ftpClient.disconnect();
                    System.err.println("FTP服务器登录失败!");
                    return false;
                }
                else System.out.println( "FTP服务器登录成功..." );
            }

            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            fos = new FileInputStream( strLocalPath );

            //blRet = ftpClient.storeFile(new String(strPath.getBytes("UTF-8"),"iso-8859-1"),fos) ;
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

    /*
     * 从FTP服务器上下载文件,支持断点续传，上传百分比汇报
     *
     * @param remote 远程文件路径
     *
     * @param local 本地文件路径
     *
     * @return 上传的状态
     *
     * @throws IOException
     */
//    public DownloadStatus download(String remote, String local)
//            throws IOException {
//        // 设置被动模式
//        ftpClient.enterLocalPassiveMode();
//        // 设置以二进制方式传输
//        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//        DownloadStatus result;
//        // 检查远程文件是否存在
//        FTPFile[] files = ftpClient.listFiles(new String(remote
//                .getBytes("UTF-8"), "iso-8859-1"));
//        if (files.length != 1) {
//            System.out.println("远程文件不存在");
//            return DownloadStatus.Remote_File_Noexist;
//        }
//        long lRemoteSize = files[0].getSize();
//        String fildName = files[0].getName();
//        // 本地存在文件，进行断点下载
//        File f = new File(local+fildName);
//        if (f.exists()) {
//            long localSize = f.length();
//            if (localSize >= lRemoteSize) {
//                System.out.println("本地文件大于远程文件，下载中止");
//                return DownloadStatus.Local_Bigger_Remote;
//            }
//
//            // 进行断点续传，并记录状态
//            FileOutputStream out = new FileOutputStream(f, true);
//            ftpClient.setRestartOffset(localSize);
//            InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("UTF-8"), "iso-8859-1"));
//            byte[] bytes = new byte[1024];
//            long step = lRemoteSize / 100;
//            long process = localSize / step;
//            int c;
//            while ((c = in.read(bytes)) != -1) {
//                out.write(bytes, 0, c);
//                localSize += c;
//                long nowProcess = localSize / step;
//                if (nowProcess > process) {
//                    process = nowProcess;
//                    if (process % 10 == 0)
//                        System.out.println("下载进度：" + process);
//                    // TODO 更新文件下载进度,值存放在process变量中
//                }
//            }
//            in.close();
//            out.close();
//            boolean isDo = ftpClient.completePendingCommand();
//            if (isDo) {
//                result = DownloadStatus.Download_From_Break_Success;
//            } else {
//                result = DownloadStatus.Download_From_Break_Failed;
//            }
//        } else {
//            OutputStream out = new FileOutputStream(f);
//            InputStream in = ftpClient.retrieveFileStream(new String(remote.getBytes("UTF-8"), "iso-8859-1"));
//            byte[] bytes = new byte[1024];
//            long step = lRemoteSize / 100;
//            long process = 0;
//            long localSize = 0L;
//            int c;
//            while ((c = in.read(bytes)) != -1) {
//                out.write(bytes, 0, c);
//                localSize += c;
//                long nowProcess = localSize / step;
//                if (nowProcess > process) {
//                    process = nowProcess;
//                    if (process % 10 == 0)
//                        System.out.println("下载进度：" + process);
//                    // TODO 更新文件下载进度,值存放在process变量中
//                }
//            }
//            in.close();
//            out.close();
//            boolean upNewStatus = ftpClient.completePendingCommand();
//            if (upNewStatus) {
//                result = DownloadStatus.Download_New_Success;
//            } else {
//                result = DownloadStatus.Download_New_Failed;
//            }
//        }
//        return result;
//    }
//
//    private void disconnect() throws IOException {
//        if (ftpClient.isConnected()) {
//            ftpClient.disconnect();
//        }
//    }
}
