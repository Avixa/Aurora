package altn72.aurora.service;

import altn72.aurora.modele.provider.Provider;
import altn72.aurora.modele.provider.ProviderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProviderService {

    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    // providers offered when assigning an incident
    public List<Provider> getProviders() {
        return providerRepository.findAll();
    }
}