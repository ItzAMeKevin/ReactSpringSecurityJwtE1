package com.lacouf.rsbjwt.seed;

import java.util.List;

record CvContent(
        String name,
        String contact,
        List<String> education,
        List<String> skills,
        List<String> experience
) {}
