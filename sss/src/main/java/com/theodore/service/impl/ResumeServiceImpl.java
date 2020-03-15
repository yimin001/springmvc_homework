package com.theodore.service.impl;

import com.theodore.dao.ResumeDao;
import com.theodore.entity.Resume;
import com.theodore.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private ResumeDao resumeDao;;

    public List<Resume> findAll() {

        return resumeDao.findAll();

    }

    @Override
    public Resume findOne(Long id) {
        return resumeDao.findById(id).get();
    }

    @Override
    public void save(Resume resume) {
        resumeDao.save(resume);
    }

    @Override
    public void delete(Long id) {
        resumeDao.deleteById(id);
    }
}
