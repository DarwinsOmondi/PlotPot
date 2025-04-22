package com.example.plotpot.customs

import com.example.plotpot.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object Supabase {
    const val suparbaseUrl: String = BuildConfig.SUPABASE_URL
    const val suparbaseKey: String = BuildConfig.SUPABASE_KEY
}

val supabase = createSupabaseClient(
    supabaseUrl = Supabase.suparbaseUrl,
    supabaseKey = Supabase.suparbaseKey
) {
    install(Auth)
    install(Postgrest)
    install(Realtime)
    install(Storage)
}