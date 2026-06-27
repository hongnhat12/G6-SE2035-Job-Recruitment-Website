package com.se2035.jrw.service;

import com.se2035.jrw.repository.CVRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CVServiceImpl implements CVService{
    private final CVRepo cvRepo;

}
