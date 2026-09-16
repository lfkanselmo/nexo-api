package com.nexo.domain.port;

import com.nexo.domain.model.LeaseContract;
import com.nexo.domain.model.Payment;
import com.nexo.domain.model.Tenant;

public interface ReminderNotifier {

    void remind(Tenant tenant, LeaseContract contract, Payment payment);
}
