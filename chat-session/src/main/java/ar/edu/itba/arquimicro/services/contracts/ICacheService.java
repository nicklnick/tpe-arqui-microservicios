package ar.edu.itba.arquimicro.services.contracts;

public interface ICacheService {

    void put(String key, String value);
    String find(String key);

}
