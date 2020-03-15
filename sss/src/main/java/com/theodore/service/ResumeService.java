package com.theodore.service;

import com.theodore.entity.Resume;

import java.util.List;

public interface ResumeService {

    List<Resume> findAll();

    Resume findOne(Long id);

    void save(Resume resume);

    void delete(Long id);
}
