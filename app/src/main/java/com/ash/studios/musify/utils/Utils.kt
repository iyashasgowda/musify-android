package com.ash.studios.musify.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ScrollView
import android.widget.Toast
import androidx.palette.graphics.Palette
import com.ash.studios.musify.R
import com.ash.studios.musify.models.*
import com.ash.studios.musify.utils.Constants.ALBUMS_SORT
import com.ash.studios.musify.utils.Constants.ALL_SONGS_SORT
import com.ash.studios.musify.utils.Constants.ARTISTS_SORT
import com.ash.studios.musify.utils.Constants.GENRES_SORT
import com.ash.studios.musify.utils.Constants.LR_SORT
import com.ash.studios.musify.utils.Constants.TR_SORT
import com.ash.studios.musify.utils.Constants.YEARS_SORT
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("InlinedApi, DefaultLocale")
object Utils {
    lateinit var years: ArrayList<Year>
    lateinit var albums: ArrayList<Album>
    lateinit var genres: ArrayList<Genre>
    lateinit var artists: ArrayList<Artist>
    lateinit var folders: ArrayList<Folder>
    val newColor: String get() = String.format("#%06X", Random().nextInt(0xFFFFFF - 0x777777 + 1) + 0x777777)

    fun getAlbumArt(id: Long): Uri {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), id)
    }

    fun getDuration(duration: Long): String {
        return String.format(
            "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration),
            TimeUnit.MILLISECONDS.toSeconds(duration) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        )
    }

    fun getDialog(c: Context?, layout: Int): Dialog {
        val dialog = Dialog(c!!)
        dialog.window!!.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.attributes.windowAnimations = R.style.dg_anim
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(layout)
        dialog.show()
        dialog.window!!.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        val scrollView = dialog.findViewById<ScrollView>(R.id.dialog_scroll_view)
        if (scrollView != null) OverScrollDecoratorHelper.setUpOverScroll(scrollView)
        return dialog
    }

    fun getSecondaryColors(c: Context, uri: Uri?): IntArray {
        val bitmap: Bitmap? = try {
            MediaStore.Images.Media.getBitmap(c.contentResolver, uri)
        } catch (e: IOException) {
            BitmapFactory.decodeResource(c.resources, R.drawable.placeholder)
        }
        val palette = Palette.from(bitmap!!).generate()
        val vibrant = palette.darkVibrantSwatch
        val muted = palette.darkMutedSwatch
        val dominant = palette.dominantSwatch
        val colors = IntArray(2)
        if (vibrant != null) {
            colors[0] = vibrant.rgb
            colors[1] = vibrant.bodyTextColor
        } else if (muted != null) {
            colors[0] = muted.rgb
            colors[1] = muted.bodyTextColor
        } else if (dominant != null) {
            colors[0] = dominant.rgb
            colors[1] = dominant.bodyTextColor
        } else {
            colors[0] = Color.parseColor("#202020")
            colors[1] = Color.WHITE
        }
        return colors
    }

    //Save and retrieve current played song position
    fun putCurrentPosition(c: Context, pos: Int) {
        c.getSharedPreferences("CURRENT_POS", Context.MODE_PRIVATE)
            .edit().putInt("current_position", pos).apply()
    }

    fun getCurrentPosition(c: Context): Int {
        return c.getSharedPreferences("CURRENT_POS", Context.MODE_PRIVATE)
            .getInt("current_position", 0)
    }

    //Save and retrieve shuffle status
    fun putShflStatus(c: Context, shfl: Boolean) {
        c.getSharedPreferences("SHUFFLE_STATUS", Context.MODE_PRIVATE)
            .edit().putBoolean("shuffle_status", shfl).apply()
    }

    fun getShflStatus(c: Context): Boolean {
        return c.getSharedPreferences("SHUFFLE_STATUS", Context.MODE_PRIVATE)
            .getBoolean("shuffle_status", false)
    }

    //Save and retrieve repeat status
    fun putRepStatus(c: Context, rep: Boolean) {
        c.getSharedPreferences("REPEAT_STATUS", Context.MODE_PRIVATE)
            .edit().putBoolean("repeat_status", rep).apply()
    }

    fun getRepStatus(c: Context): Boolean {
        return c.getSharedPreferences("REPEAT_STATUS", Context.MODE_PRIVATE)
            .getBoolean("repeat_status", false)
    }

    //Save and retrieve current played song list
    fun putCurrentList(c: Context, songs: ArrayList<Song>) {
        c.getSharedPreferences("CURRENT_LIST", Context.MODE_PRIVATE)
            .edit().putString(
                "current_list", Gson()
                    .toJson(songs)
            ).apply()
    }

    fun getCurrentList(c: Context): ArrayList<Song> {
        val s = c.getSharedPreferences("CURRENT_LIST", Context.MODE_PRIVATE).getString("current_list", null)
        return if (s != null) Gson().fromJson(s, object : TypeToken<ArrayList<Song?>?>() {}.type)
        else ArrayList()
    }

    //Get all songs and songs with categories
    fun getAllSongs(c: Context): ArrayList<Song> {
        val prefs = c.getSharedPreferences(ALL_SONGS_SORT, Context.MODE_PRIVATE)
        val orderBy = if (prefs.getBoolean("order_by", false)) "desc" else "asc"
        val sortBy = prefs.getString("sort_by", "title")
        val songs = ArrayList<Song>()
        val selection = "is_music != 0"
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.ALBUM_ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = c.contentResolver.query(
            uri,
            projection,
            selection,
            null,
            "$sortBy $orderBy"
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(0)
                val data = cursor.getString(1)
                val title = cursor.getString(2)
                val album = cursor.getString(3)
                val artist = cursor.getString(4)
                val year = cursor.getString(5)
                val track = cursor.getString(6)
                val composer = cursor.getString(7)
                val albumArtist = cursor.getString(8)
                val duration = cursor.getLong(9)
                val albumId = cursor.getLong(10)
                if (title != null && album != null && artist != null && year != null && track != null && composer != null && albumArtist != null) if (title != "<unknown>") songs.add(
                    Song(
                        id,
                        data,
                        title,
                        album,
                        artist,
                        year,
                        track,
                        composer,
                        albumArtist,
                        duration,
                        albumId
                    )
                )
            }
            cursor.close()
        }
        return songs
    }

    fun getAllSongsByCategory(c: Context, sortBy: String?): ArrayList<Song> {
        val songs = ArrayList<Song>()
        val selection = "is_music != 0"
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.ALBUM_ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = c.contentResolver.query(
            uri,
            projection,
            selection,
            null,
            sortBy
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(0)
                val data = cursor.getString(1)
                val title = cursor.getString(2)
                val album = cursor.getString(3)
                val artist = cursor.getString(4)
                val year = cursor.getString(5)
                val track = cursor.getString(6)
                val composer = cursor.getString(7)
                val albumArtist = cursor.getString(8)
                val duration = cursor.getLong(9)
                val albumId = cursor.getLong(10)
                if (title != null && album != null && artist != null && albumArtist != null && composer != null && year != null) if (title != "<unknown>") songs.add(
                    Song(
                        id,
                        data,
                        title,
                        album,
                        artist,
                        year,
                        track,
                        composer,
                        albumArtist,
                        duration,
                        albumId
                    )
                )
            }
            cursor.close()
        }
        return songs
    }

    //Save, fetch and delete top-rated
    fun getTR(c: Context): ArrayList<Song> {
        val prefs = c.getSharedPreferences(TR_SORT, Context.MODE_PRIVATE)
        var list = Gson().fromJson<ArrayList<Song>>(
            PreferenceManager.getDefaultSharedPreferences(c).getString("TOP_RATED", null),
            object : TypeToken<ArrayList<Song>>() {}.type
        )
        if (list == null) list = ArrayList()
        val sortBy = prefs.getString("sort_by", "title")
        val isReverse = prefs.getBoolean("order_by", false)
        when (sortBy) {
            "title" -> list.sortWith(Comparator.comparing(Song::title))
            "album" -> list.sortWith(Comparator.comparing(Song::album))
            "artist" -> list.sortWith(Comparator.comparing(Song::artist))
        }
        if (isReverse) list.reverse()
        return list
    }

    fun saveToTR(c: Context?, song: Song?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(c)
        var list = Gson().fromJson<ArrayList<Song?>>(
            prefs.getString("TOP_RATED", null),
            object : TypeToken<ArrayList<Song?>?>() {}.type
        )
        if (list != null) {
            if (!list.contains(song)) list.add(song) else return
        } else {
            list = ArrayList()
            list.add(song)
        }
        val editor = prefs.edit()
        editor.putString("TOP_RATED", Gson().toJson(list)).apply()
    }

    fun deleteFromTR(c: Context?, song: Song) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(c)
        val list = Gson().fromJson<ArrayList<Song>>(
            prefs.getString("TOP_RATED", null),
            object : TypeToken<ArrayList<Song?>?>() {}.type
        )
        list?.remove(song)
        val editor = prefs.edit()
        editor.putString("TOP_RATED", Gson().toJson(list)).apply()
    }

    //Save, fetch and delete low-rated
    fun getLR(c: Context): ArrayList<Song> {
        val prefs = c.getSharedPreferences(LR_SORT, Context.MODE_PRIVATE)
        var list = Gson().fromJson<ArrayList<Song>>(
            PreferenceManager.getDefaultSharedPreferences(c).getString("LOW_RATED", null),
            object : TypeToken<ArrayList<Song>>() {}.type
        )
        if (list == null) list = ArrayList()
        val sortBy = prefs.getString("sort_by", "title")
        val isReverse = prefs.getBoolean("order_by", false)
        when (sortBy) {
            "title" -> list.sortWith(Comparator.comparing(Song::title))
            "album" -> list.sortWith(Comparator.comparing(Song::album))
            "artist" -> list.sortWith(Comparator.comparing(Song::artist))
        }
        if (isReverse) list.reverse()
        return list
    }

    fun saveToLR(c: Context, song: Song) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(c)
        var list = Gson().fromJson<ArrayList<Song?>>(
            prefs.getString("LOW_RATED", null),
            object : TypeToken<ArrayList<Song>>() {}.type
        )
        if (list != null) {
            if (!list.contains(song)) list.add(song) else return
        } else {
            list = ArrayList()
            list.add(song)
        }
        val editor = prefs.edit()
        editor.putString("LOW_RATED", Gson().toJson(list)).apply()
    }

    fun deleteFromLR(c: Context, song: Song) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(c)
        val list = Gson().fromJson<ArrayList<Song>>(
            prefs.getString("LOW_RATED", null),
            object : TypeToken<ArrayList<Song>>() {}.type
        )
        list?.remove(song)
        val editor = prefs.edit()
        editor.putString("LOW_RATED", Gson().toJson(list)).apply()
    }

    //Get category based songs
    fun getGenreSongs(c: Context, genreId: Long): ArrayList<Song> {
        val songs = ArrayList<Song>()
        val uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId)
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.ALBUM_ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = c.contentResolver.query(
            uri,
            projection,
            null,
            null,
            "title asc"
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(0)
                val data = cursor.getString(1)
                val title = cursor.getString(2)
                val album = cursor.getString(3)
                val artist = cursor.getString(4)
                val year = cursor.getString(5)
                val track = cursor.getString(6)
                val composer = cursor.getString(7)
                val albumArtist = cursor.getString(8)
                val duration = cursor.getLong(9)
                val albumId = cursor.getLong(10)
                if (title != null && album != null && artist != null && composer != null && albumArtist != null && year != null) if (title != "<unknown>") songs.add(
                    Song(
                        id,
                        data,
                        title,
                        album,
                        artist,
                        year,
                        track,
                        composer,
                        albumArtist,
                        duration,
                        albumId
                    )
                )
            }
            cursor.close()
        }
        return songs
    }

    fun getAlbumSongs(c: Context, albumId: Long): ArrayList<Song> {
        val songs = ArrayList<Song>()
        var selection = "is_music != 0"
        if (albumId > 0) selection = "$selection and album_id = $albumId"
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.ALBUM_ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = c.contentResolver.query(
            uri,
            projection,
            selection,
            null,
            "title asc"
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(0)
                val data = cursor.getString(1)
                val title = cursor.getString(2)
                val album = cursor.getString(3)
                val artist = cursor.getString(4)
                val year = cursor.getString(5)
                val track = cursor.getString(6)
                val composer = cursor.getString(7)
                val albumArtist = cursor.getString(8)
                val duration = cursor.getLong(9)
                val albumId1 = cursor.getLong(10)
                if (title != null && album != null && artist != null) if (title != "<unknown>") songs.add(
                    Song(
                        id,
                        data,
                        title,
                        album,
                        artist,
                        year,
                        track,
                        composer,
                        albumArtist,
                        duration,
                        albumId1
                    )
                )
            }
            cursor.close()
        }
        return songs
    }

    fun getArtistSongs(c: Context, artistId: Long): ArrayList<Song> {
        val songs = ArrayList<Song>()
        var selection = "is_music != 0"
        if (artistId > 0) selection = "$selection and artist_id = $artistId"
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.ALBUM_ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = c.contentResolver.query(
            uri,
            projection,
            selection,
            null,
            "title asc"
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(0)
                val data = cursor.getString(1)
                val title = cursor.getString(2)
                val album = cursor.getString(3)
                val artist = cursor.getString(4)
                val year = cursor.getString(5)
                val track = cursor.getString(6)
                val composer = cursor.getString(7)
                val albumArtist = cursor.getString(8)
                val duration = cursor.getLong(9)
                val albumId = cursor.getLong(10)
                if (title != null && album != null && artist != null && year != null && track != null && albumArtist != null && composer != null) if (title != "<unknown>") songs.add(
                    Song(
                        id,
                        data,
                        title,
                        album,
                        artist,
                        year,
                        track,
                        composer,
                        albumArtist,
                        duration,
                        albumId
                    )
                )
            }
            cursor.close()
        }
        return songs
    }

    private fun getFolderSongs(c: Context, path: String): ArrayList<Song> {
        val songs = ArrayList<Song>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.ALBUM_ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val selection = MediaStore.Audio.Media.DATA + " like ?"
        val selectionArgs = arrayOf("$path%")
        val cursor = c.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(0)
                val data = cursor.getString(1)
                val title = cursor.getString(2)
                val album = cursor.getString(3)
                val artist = cursor.getString(4)
                val year = cursor.getString(5)
                val track = cursor.getString(6)
                val composer = cursor.getString(7)
                val albumArtist = cursor.getString(8)
                val duration = cursor.getLong(9)
                val albumId = cursor.getLong(10)
                if (title != null && album != null && artist != null && year != null && composer != null && albumArtist != null) if (title != "<unknown>") songs.add(
                    Song(
                        id,
                        data,
                        title,
                        album,
                        artist,
                        year,
                        track,
                        composer,
                        albumArtist,
                        duration,
                        albumId
                    )
                )
            }
            cursor.close()
        }
        return songs
    }

    //Save, update and delete playlists
    fun createNewPlaylist(c: Context, name: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(c)
        var list = Gson().fromJson<ArrayList<Playlist>>(
            prefs.getString("PLAYLISTS", null),
            object : TypeToken<ArrayList<Playlist>>() {}.type
        )
        val playlist = Playlist(name, ArrayList())
        if (list != null) {
            if (!list.contains(playlist)) list.add(playlist) else return
        } else {
            list = ArrayList()
            list.add(playlist)
        }
        val editor = prefs.edit()
        editor.putString("PLAYLISTS", Gson().toJson(list)).apply()
    }

    fun addToPlaylist(c: Context, position: Int, song: Song) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(c)
        val list = Gson().fromJson<ArrayList<Playlist>>(
            prefs.getString("PLAYLISTS", null),
            object : TypeToken<ArrayList<Playlist?>?>() {}.type
        )

        //Clearing the list
        val editor = prefs.edit()
        editor.remove("PLAYLISTS")

        //Checking if song already exist in any of the playlists
        if (list != null) {
            val playlist = list[position]
            if (playlist.songs.contains(song)) {
                Toast.makeText(c, "Song already exist in that playlist", Toast.LENGTH_SHORT).show()
                return
            }

            //Removing the playlist before add a new one with song included
            list.removeAt(position)

            //Adding song to the playlist and playlist to the playlists
            playlist.songs.add(song)
            list.add(playlist)

            //Saving back the playlists to the shared preference
            editor.putString("PLAYLISTS", Gson().toJson(list)).apply()
            Toast.makeText(c, song.title + " added to the " + playlist.name, Toast.LENGTH_SHORT).show()
        }
    }

    fun delFrmPlaylist(c: Context, position: Int, song: Song) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(c)
        val list = Gson().fromJson<ArrayList<Playlist>>(
            prefs.getString("PLAYLISTS", null),
            object : TypeToken<ArrayList<Playlist>>() {}.type
        )

        //Clearing the list
        val editor = prefs.edit()
        editor.remove("PLAYLISTS")
        if (list != null) {
            val playlist = list[position]
            list.removeAt(position)
            playlist.songs.remove(song)
            list.add(playlist)
            editor.putString("PLAYLISTS", Gson().toJson(list)).apply()
        }
    }

    fun deletePlaylist(c: Context, pl: Playlist) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(c)
        val list = Gson().fromJson<ArrayList<Playlist>>(
            prefs.getString("PLAYLISTS", null),
            object : TypeToken<ArrayList<Playlist?>?>() {}.type
        )
        list?.remove(pl)
        val editor = prefs.edit()
        editor.putString("PLAYLISTS", Gson().toJson(list)).apply()
    }

    //Get categories
    fun getFolders(c: Context): ArrayList<Folder> {
        val dataList = ArrayList<String>()
        val folders = ArrayList<Folder>()
        val names = ArrayList<String>()
        val paths = ArrayList<String>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " = 1"
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME
        )
        val cursor = c.contentResolver.query(
            uri,
            projection,
            selection,
            null,
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                dataList.add(cursor.getString(0))
                names.add(cursor.getString(1))
            }
            for (i in dataList.indices) paths.add(dataList[i].replace(names[i], ""))
            val hs = HashSet(paths)
            paths.clear()
            paths.addAll(hs)
            cursor.close()
        }
        for (path in paths) {
            val folderName = File(path).name
            val songs = getFolderSongs(c, path)
            if (songs.size > 0) folders.add(Folder(folderName, path, songs))
        }
        return folders
    }

    fun getAlbums(c: Context): ArrayList<Album> {
        val prefs = c.getSharedPreferences(ALBUMS_SORT, Context.MODE_PRIVATE)
        val orderBy = if (prefs.getBoolean("order_by", false)) "desc" else "asc"
        val sortBy = prefs.getString("sort_by", "album")
        val albums = ArrayList<Album>()
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS
        )
        val cursor = c.contentResolver.query(
            uri,
            projection,
            null,
            null,
            "$sortBy $orderBy"
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val albumId = cursor.getLong(0)
                val album = cursor.getString(1)
                val artist = cursor.getString(2)
                val noOfSongs = cursor.getInt(3)
                if (album != "<unknown>") albums.add(
                    Album(
                        album,
                        artist,
                        albumId,
                        noOfSongs
                    )
                )
            }
            cursor.close()
        }
        return albums
    }

    fun getArtists(c: Context): ArrayList<Artist> {
        val prefs = c.getSharedPreferences(ARTISTS_SORT, Context.MODE_PRIVATE)
        val orderBy = if (prefs.getBoolean("order_by", false)) "desc" else "asc"
        val sortBy = prefs.getString("sort_by", "artist")
        val artists = ArrayList<Artist>()
        val uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        )
        val cursor = c.contentResolver.query(
            uri,
            projection,
            null,
            null,
            "$sortBy $orderBy"
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val artistId = cursor.getLong(0)
                val artist = cursor.getString(1)
                val noOfSongs = cursor.getInt(2)
                val songs = getArtistSongs(c, artistId)
                if (songs.size > 0) if (artist != "<unknown>") artists.add(
                    Artist(
                        artistId,
                        artist,
                        noOfSongs,
                        getArtistSongs(c, artistId)[0].album_id
                    )
                )
            }
            cursor.close()
        }
        return artists
    }

    fun getGenres(c: Context): ArrayList<Genre> {
        val prefs = c.getSharedPreferences(GENRES_SORT, Context.MODE_PRIVATE)
        val orderBy = if (prefs.getBoolean("order_by", false)) "desc" else "asc"
        val sortBy = prefs.getString("sort_by", "name")
        val genres = ArrayList<Genre>()
        val uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Genres._ID,
            MediaStore.Audio.Genres.NAME
        )
        val cursor = c.contentResolver.query(
            uri,
            projection,
            null,
            null,
            "$sortBy $orderBy"
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val genresId = cursor.getLong(0)
                val genre = cursor.getString(1)
                val songs = getGenreSongs(c, genresId)
                if (songs.size > 0) genres.add(
                    Genre(
                        genresId,
                        genre,
                        songs[0].album_id,
                        songs.size
                    )
                )
            }
            cursor.close()
        }
        return genres
    }

    fun getPlaylists(c: Context): ArrayList<Playlist> {
        return Gson().fromJson(
            PreferenceManager.getDefaultSharedPreferences(c).getString("PLAYLISTS", null),
            object : TypeToken<ArrayList<Playlist>>() {}.type
        )
    }

    fun getYears(c: Context): ArrayList<Year> {
        val prefs = c.getSharedPreferences(YEARS_SORT, Context.MODE_PRIVATE)
        val isReverse = prefs.getBoolean("order_by", false)
        val list = getAllSongsByCategory(c, "year desc")
        val years = ArrayList<Year>()
        val yearsList = ArrayList<String>()
        var i = 0
        while (i < list.size) {
            if (!yearsList.contains(list[i].year)) {
                val presentYear = list[i].year
                yearsList.add(presentYear)
                val songsList = ArrayList<Song>()
                for (j in i until list.size) {
                    if (list[j].year == presentYear) {
                        songsList.add(list[j])
                    } else {
                        i = j
                        break
                    }
                }
                val bitmap: Bitmap? = try {
                    MediaStore.Images.Media.getBitmap(
                        c.contentResolver,
                        getAlbumArt(songsList[0].album_id)
                    )
                } catch (e: Exception) {
                    BitmapFactory.decodeResource(c.resources, R.drawable.placeholder)
                }
                if (songsList.size > 0) years.add(Year(presentYear, bitmap!!, songsList))
            }
            i++
        }
        if (isReverse) years.reverse()
        return years
    }

    fun fetchAllSongs(c: Context) {
        Thread { genres = getGenres(c) }.start()
        Thread { albums = getAlbums(c) }.start()
        Thread { artists = getArtists(c) }.start()
        Thread { years = getYears(c) }.start()
        Thread { folders = getFolders(c) }.start()
    }
}