package com.github.anrimian.musicplayer;

public interface Constants {

    int NO_POSITION = -1;

    interface Actions {
        int PLAY = 1;
        int PAUSE = 2;
        int SKIP_TO_NEXT = 3;
        int SKIP_TO_PREVIOUS = 4;
        int CHANGE_SHUFFLE_NODE = 5;
        int CHANGE_REPEAT_MODE = 6;
    }

    interface Arguments {
        String ORDER_ARG = "order_arg";
        String PLAY_LIST_ID_ARG = "play_list_id_arg";
        String ID_ARG = "id_arg";//we can replace other `id_arg`
        String PATH_ARG = "path_arg";
        String STATUS_BAR_COLOR_ATTR_ARG = "status_bar_color_attr";
        String OPEN_PLAY_QUEUE_ARG = "open_play_queue_arg";
        String COMPOSITION_NAME_ARG = "composition_name_arg";
        String COMPOSITION_AUTHOR_ARG = "composition_author_arg";
        String COMPOSITION_FILE_ARG = "composition_file_arg";
        String COMPOSITION_ID_ARG = "composition_id_arg";
        String PLAY_ARG = "play_arg";
        String QUEUE_SIZE_ARG = "queue_size_arg";
        String TITLE_ARG = "title_arg";
        String POSITIVE_BUTTON_ARG = "positive_button_arg";
        String NEGATIVE_BUTTON_ARG = "negative_button_arg";
        String EDIT_TEXT_HINT = "edit_text_hint";
        String EDIT_TEXT_VALUE = "edit_text_value";
        String CAN_BE_EMPTY_ARG = "can_be_empty_arg";
        String EXTRA_DATA_ARG = "extra_data_arg";
        String COMPOSITION_ARG = "composition_arg";
        String MENU_ARG = "menu_arg";
        String POSITION_ARG = "position_arg";
        String HINTS_ARG = "hints_arg";
    }

    interface Tags {
        String ORDER_TAG = "order_tag";
        String SELECT_PLAYLIST_TAG = "select_playlist_tag";
        String SELECT_PLAYLIST_FOR_FOLDER_TAG = "select_playlist_for_folder_tag";
        String CREATE_PLAYLIST_TAG = "create_playlist_tag";
        String PLAY_LIST_MENU = "play_list_menu";
        String COMPOSITION_ACTION_TAG = "composition_action_tag";
        String AUTHOR_TAG = "author_tag";
        String TITLE_TAG = "title_tag";
        String FILE_NAME_TAG = "file_name_tag";
        String ALBUM_TAG = "album_tag";
        String GENRE_TAG = "genre_tag";
        String NEW_FOLDER_NAME_TAG = "new_folder_name_tag";
        String ALBUM_MENU_TAG = "album_menu_tag";
        String ALBUM_NAME_TAG = "album_name_tag";
        String MESSAGE_ARG = "message_arg";
        String PROGRESS_DIALOG_TAG = "progress_dialog_arg";
    }

    interface Animation {
        int TOOLBAR_ARROW_ANIMATION_TIME = 200;
    }
}
