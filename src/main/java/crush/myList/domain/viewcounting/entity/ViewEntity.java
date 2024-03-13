package crush.myList.domain.viewcounting.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

public interface ViewEntity {
    View getView();
    void setView(View view);
}
