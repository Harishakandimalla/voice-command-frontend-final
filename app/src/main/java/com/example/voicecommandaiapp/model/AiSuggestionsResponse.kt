package com.example.voicecommandaiapp.model

data class AiSuggestionsResponse(
    val ok: Boolean,
    val data: List<AiSuggestion>
)
