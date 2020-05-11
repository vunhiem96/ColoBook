/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.volio.coloringbook2.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.volio.coloringbook2.model.storybook.saveLocal.ImageStorySave
import java.util.*


object Converters {
    @TypeConverter
    @JvmStatic
    fun storedStringToMyObjects(data: String?): List<ImageStorySave?>? {
        val gson = Gson()
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object : TypeToken<List<ImageStorySave?>?>() {}.type
        return gson.fromJson<List<ImageStorySave?>>(data, listType)
    }

    @TypeConverter
    @JvmStatic
    fun myObjectsToStoredString(myObjects: List<ImageStorySave?>?): String? {
        val gson = Gson()
        return gson.toJson(myObjects)
    }

}
