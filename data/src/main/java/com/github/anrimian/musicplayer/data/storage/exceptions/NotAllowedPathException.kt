package com.github.anrimian.musicplayer.data.storage.exceptions

class NotAllowedPathException(allowedFolders: String? = null): RuntimeException(allowedFolders)