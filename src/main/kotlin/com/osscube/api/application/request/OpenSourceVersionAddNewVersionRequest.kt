package com.osscube.api.application.request

class OpenSourceVersionAddNewVersionRequest(
    val version: String,
    val sourceUrl: String?,
    val licenses: List<LicenseAddRequest> = mutableListOf()
)
