package org.efrei.argosbackend.domain.exception;

public class ProviderUnderSurveillanceException extends BusinessRuleException {

    public ProviderUnderSurveillanceException(String providerName) {
        super(String.format("Le prestataire %s est sous surveillance : la moyenne de ses dernières évaluations est inférieure au seuil. "
                + "Confirmez l'affectation pour l'enregistrer.", providerName));
    }
}
