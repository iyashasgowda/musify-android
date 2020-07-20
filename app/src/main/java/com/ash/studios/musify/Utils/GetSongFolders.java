package com.ash.studios.musify.Utils;

import java.io.File;
import java.io.FileFilter;

public class GetSongFolders {

    /*public static List<Folder> getSongFolders(Context context) {
        File dir = new File(Environment.getExternalStorageDirectory().getPath());
        Toast.makeText(context, "" + dir, Toast.LENGTH_SHORT).show();
        List<Song> songList = new ArrayList<>();
        List<Folder> folderList = new ArrayList<>();

        File[] scannedFolders = dir.listFiles(new AudioFilter());
        if (scannedFolders == null) return null;

        for (File scannedFolder : scannedFolders) {
            Folder folder = new Folder();

            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media.DATA}, MediaStore.Audio.Media.DATA + " like ? ",
                    new String[]{"%" + scannedFolder.getAbsolutePath() + "%"}, null);

            if (cursor != null) {
                int count = cursor.getCount();

                if (!scannedFolder.isDirectory()) {
                    String path = scannedFolder.getAbsolutePath();
                    Song song = Utils.getFolderSong(MediaStore.Audio.Media.DEFAULT_SORT_ORDER, context, path);
                    songList.add(song);
                }
                if (!scannedFolder.getAbsolutePath().startsWith("/d") || count > 0) {
                    folder.setFile(scannedFolder);
                    folder.setFile_count(count);
                    folder.setSongs(songList);
                    folderList.add(folder);
                }
            }
            if (cursor != null) cursor.close();
        }

        Collections.sort(folderList, new Comparator<Folder>() {
            @Override
            public int compare(Folder f1, Folder f2) {
                if ((f1.getFile().isDirectory() && f2.getFile().isDirectory()))
                    return f1.getFile().getName().compareToIgnoreCase(f2.getFile().getName());
                else if (f1.getFile().isDirectory() && !f2.getFile().isDirectory())
                    return -1;
                else if (!f1.getFile().isDirectory() && f2.getFile().isDirectory())
                    return 1;
                else if (!f1.getFile().isDirectory() && !f2.getFile().isDirectory())
                    return f1.getFile().getName().compareToIgnoreCase(f2.getFile().getName());
                else return 0;
            }
        });

        if (!dir.getAbsolutePath().equals("/")) {
            Folder folder = new Folder();
            if (dir.getParentFile() != null) {
                folder.setFile(dir.getParentFile());
                folderList.add(0, folder);
            }
        }
        return folderList;
    }*/

    public static class AudioFilter implements FileFilter {
        private String[] extension = {".aac", ".mp3", ".wav", ".ogg", ".midi", ".3gp", ".mp4", ".m4a", ".amr", ".flac"};

        @Override
        public boolean accept(File pathname) {

            if ((pathname.isDirectory() || pathname.isFile()) && !pathname.isHidden()) return true;
            for (String ext : extension)
                if (pathname.getName().toLowerCase().endsWith(ext)) return true;

            return false;
        }
    }
}
