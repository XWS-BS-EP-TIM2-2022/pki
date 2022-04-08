package com.example.PKI.service;

import java.util.List;

public interface Service<T> {
	public T findOne(long id);
    public List<T> findAll();
    public T save(T appUser) ;
    public void remove(long id);
    public T findByEmail(String email);
}
