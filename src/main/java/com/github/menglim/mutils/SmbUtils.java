package com.github.menglim.mutils;

import com.github.menglim.mutils.AppUtils;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.security.bc.BCSecurityProvider;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


@Slf4j
public class SmbUtils {

//    static {
//        Security.addProvider(new BouncyCastleProvider());
//    }

    //https://programmer.ink/think/application-of-smb-in-java.html
//    <dependency>
//            <groupId>jcifs</groupId>
//            <artifactId>jcifs</artifactId>
//            <version>1.3.17</version>
//        </dependency>
//    public static void downloadSmbFile(String remoteUrl, String shareFolderPath, String fileName, String localDir) {
//        InputStream in = null;
//        OutputStream out = null;
//        try {
//            SmbFile smbfile = new SmbFile(remoteUrl + shareFolderPath + File.separator + fileName);
//            File localFile = new File(localDir + File.separator + fileName);
//            in = new BufferedInputStream(new SmbFileInputStream(smbfile));
//            out = new BufferedOutputStream(new FileOutputStream(localFile));
//            FileCopyUtils.copy(in, out);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            closeStreanm(in, out);
//        }
//    }

//    public boolean uploadFileViaSmb(String host, String username, String password, String shareFolderPath, String localFullPathFileName) {
//        OutputStream out = null;
//        boolean result = true;
//        try {
//            File localFile = new File(localFullPathFileName);
//            log.info(localFullPathFileName + " is found ");
//            NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication(host, username, password);
////            String smbUrl = "smb://" + username + ":" + password + "@" + host + shareFolderPath + File.separator + localFile.getName();
//            String smbUrl = "smb://" + host + shareFolderPath + File.separator + localFile.getName();
//            log.info("Copying to " + smbUrl);
//            SmbFile smbfile = new SmbFile(smbUrl, authentication);
//            out = new BufferedOutputStream(new SmbFileOutputStream(smbfile));
//            Files.copy(Paths.get(localFullPathFileName), out);
//            log.info(localFile.getName() + " is copied");
//            result = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            result = false;
//        } finally {
//            if (out != null) {
//                try {
//                    out.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return result;
//    }

//    public static boolean uploadFileViaSmb(String host, String username, String password, String shareFolderPath, String localFullPathFileName) {
//        CIFSContext base = SingletonContext.getInstance();
//        CIFSContext authed1 = base.withCredentials(new NtlmPasswordAuthentication(base, host,
//                username, password));
//        try (SmbFile f = new SmbFile("smb://" + host + shareFolderPath, authed1)) {
//            if (f.exists()) {
//                for (SmbFile file : f.listFiles()) {
//                    System.out.println(file.getName());
//                }
//            }
//        } catch (MalformedURLException | SmbException e) {
//            e.printStackTrace();
//        }
//        return true;
//    }

    //    https://docs.microsoft.com/en-us/windows-server/storage/file-server/troubleshoot/detect-enable-and-disable-smbv1-v2-v3
    public static String splitGetAtFirst(String value, String separator) {
        String[] tmp = StringUtils.split(value, separator);
        if (tmp.length > 0) {
            return tmp[0];
        }
        return value;
    }

    public static boolean uploadFileViaSmb(String host, String username, String domainName, String password, String remoteNetworkPath, String... localFullPathFileNames) {
        boolean result = false;
        SmbConfig cfg = SmbConfig.builder().
                withMultiProtocolNegotiate(true).
                withSecurityProvider(new BCSecurityProvider()).
                build();
        SMBClient client = new SMBClient(cfg);

        try (Connection connection = client.connect(host)) {
            log.info("connected SMB Host " + host);
            AuthenticationContext ac = new AuthenticationContext(username, password.toCharArray(), domainName);
            Session session = connection.authenticate(ac);
            log.info("Authenticated");
            remoteNetworkPath = StringUtils.replace(remoteNetworkPath, "/", File.separator);
            remoteNetworkPath = StringUtils.replace(remoteNetworkPath, "\\", File.separator);
            String shareDisk = splitGetAtFirst(remoteNetworkPath, File.separator);
            log.info("ShareDisk Name => " + shareDisk);
            try (DiskShare share = (DiskShare) session.connectShare(shareDisk)) {
                String remoteDirectory = StringUtils.replaceOnce(remoteNetworkPath, shareDisk, "");
                String[] subFolders = StringUtils.split(remoteDirectory, File.separator);
                remoteDirectory = "";
                for (String aSubFolder : subFolders) {
                    if (AppUtils.getInstance().nonNull(aSubFolder)) {
                        remoteDirectory = remoteDirectory + File.separator + aSubFolder;
                        boolean folderExisted = share.folderExists(remoteDirectory);
                        log.info("RemoteDirectory " + remoteDirectory + " existed " + folderExisted);
                        if (!folderExisted) {
                            share.mkdir(remoteDirectory);
                            log.info(remoteDirectory + " is created");
                        }
                    }
                }
                for (String localFullPathFileName : localFullPathFileNames) {
                    File fileToBeUpload = new File(localFullPathFileName);
                    com.hierynomus.smbj.share.File output = null;
                    if (fileToBeUpload != null) {
                        if (!share.fileExists(remoteDirectory + File.separator + fileToBeUpload.getName())) {
                            output = share.openFile(remoteDirectory + File.separator + fileToBeUpload.getName(),
                                    new HashSet<>(Arrays.asList(AccessMask.MAXIMUM_ALLOWED)),
                                    new HashSet<>(Arrays.asList(FileAttributes.FILE_ATTRIBUTE_NORMAL)),
                                    SMB2ShareAccess.ALL,
                                    SMB2CreateDisposition.FILE_CREATE,
                                    new HashSet<>(Arrays.asList(SMB2CreateOptions.FILE_DIRECTORY_FILE)));
                            OutputStream os = output.getOutputStream();
                            os.write(Files.readAllBytes(Paths.get(localFullPathFileName)));
                            os.flush();
                            os.close();
                            output.close();
                            log.info(fileToBeUpload.getName() + " has been uploaded to " + remoteNetworkPath);
                            result = true;
                        } else {
                            log.info(fileToBeUpload.getName() + " already existed in " + remoteNetworkPath);
                        }
                    } else {
                        log.error(localFullPathFileName + " does not exist");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<File> listf(String directoryName) {
        File directory = new File(directoryName);

        List<File> resultList = new ArrayList<>();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        resultList.addAll(Arrays.asList(fList));
        for (File file : fList) {
            if (file.isFile()) {
                //System.out.println(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                resultList.addAll(listf(file.getAbsolutePath()));
            }
        }
        //System.out.println(fList);
        return resultList;
    }

    public static void main(String[] args) throws IOException {
        //SmbUtils.uploadFileViaSmb("172.17.16.243", "mfe", "bic.local", "Bic@123*", "s/PrivateDepartmentShare/Settlerecon/Reports/ATMTerminal/1011/edata", "C:\\var\\users.json");

        listf("C:\\home\\opr\\atm\\atm1011").forEach(file -> {
            System.out.println("=> " + file.getAbsolutePath());
        });
    }
}

