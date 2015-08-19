package org.ecad.captacao.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;

import org.ecad.captacao.exception.MandatoryFieldsException;
import org.ecad.captacao.persistence.AppToken;

@Stateless
public class AppTokenDAO extends AbstractDAO<Long, AppToken> {
	
	public String getEntityName() {
		return "Token de Acesso";
	}

	@Override
	public Set<String> orderOptions() {
		Set<String> orderOptions = new HashSet<>();
		
		orderOptions.add("id");
		orderOptions.add("generateDate");
		orderOptions.add("token");
		orderOptions.add("duration");
		
		return orderOptions;
	}
	
	public List<String> listTokens(Boolean valid) throws MandatoryFieldsException {
		if(valid == null) {
			throw new MandatoryFieldsException("valid é obrigatório");
		}
		
		return manager.createQuery("select t.token from AppToken t where t.validToken = :valid", String.class).setParameter("valid", valid).getResultList();
	}

	public Boolean exists(String token) throws MandatoryFieldsException {
		if(token == null) {
			throw new MandatoryFieldsException("token é obrigatório");
		}
		
		Long result = manager.createQuery("select count(t) from AppToken t where t.token = :token", Long.class).setParameter("token", token).getSingleResult();
		
		return result > 0;
	}
	
	public AppToken findByToken(String token) throws MandatoryFieldsException {
		if(token == null) {
			throw new MandatoryFieldsException("token é obrigatório");
		}
		
		return manager.createQuery("select t from AppToken t where t.token = :token", AppToken.class).setParameter("token", token).getSingleResult();
	}

	public void discardAllTokens() {
		manager.createQuery("update AppToken set validToken = :valid").setParameter("valid", false).executeUpdate();
	}
}