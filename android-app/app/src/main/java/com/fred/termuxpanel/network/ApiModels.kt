package com.fred.termuxpanel.network

data class ScriptInfo(
    val name: String,
    val pid: Int?,
    val status: String, // online, stopped, errored
    val uptime: Long?,
    val restarts: Int?
)

data class ScriptsResponse(val scripts: List<ScriptInfo>)

data class ServerStatus(
    val ram_used_pct: Int,
    val load_avg: String,
    val uptime_days: String,
    val tunnel_status: String
)

data class BotStatus(
    val connected: Boolean,
    val name: String?
)

data class ActionRequest(val type: String, val target: String)
data class ActionResponse(val ok: Boolean)
