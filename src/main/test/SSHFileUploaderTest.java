import com.github.menglim.mutils.AppUtils;

public class SSHFileUploaderTest {

    public static void main(String[] args) {
//        AppUtils.getInstance().uploadFileViaSSH(
//                "10.10.31.18",
//                "root",
//                "root",
//                "C:\\opt\\TxCBSFileProcessor\\CASA_16122020_145636.csv",
//                "/opt"
//        );

//        FileSystemOptions fsOptions = new FileSystemOptions();
//        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(fsOptions, "no");
//        /* Using the following line will cause VFS to choose File System's Root as VFS's root.
//         * If I wanted to use User's home as VFS's root then I had to set 2nd method parameter
//         * to "true"         */
//        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fsOptions, false);
//        FileSystemManager fsManager = VFS.getManager();
//
////                "sftp://" + username + ":" + password + "@" + host + "/" + remoteDirectory + "/" + local.getName());
//        String uri = "sftp://" + "root:root" + "@10.10.31.18/CASA_16122020_145636.csv";
//        FileObject fo = fsManager.resolveFile(uri, fsOptions);
//
//        FileObject appFolder = null;
//
//        appFolder = fo.resolveFile("C:\\opt\\TxCBSFileProcessor\\CASA_16122020_145636.csv", NameScope.DESCENDENT_OR_SELF);
//
//        //List content of folder
//        if (appFolder.isReadable()) {
//            System.out.println("\n Get Children of appFolder");
//            FileObject[] children = appFolder.getChildren();
//            for (int i = 0; i < children.length; i++) {
//                System.out.println(children[i].getName().getBaseName());
//            }
//        }
//
//        fo.close();
//        fsManager.close();
    }
}
