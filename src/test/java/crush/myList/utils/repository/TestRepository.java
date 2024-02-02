package crush.myList.utils.repository;

import crush.myList.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestRepository<T extends BaseEntity> {
    protected final List<T> list = new ArrayList<>();
    Long nextId() {
    return list.size() + 1L;
}

    public Optional<T> findById(Long id) {
        return Optional.of(list.get(id.intValue() - 1));
    }

    public T save(T role) {
        role.setId(nextId());
        list.add(role);
        return role;
    }

    public void delete(T role) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null) continue;

            if (list.get(i).getId().equals(role.getId())) {
                list.set(i, null);
                break;
            }
        }
    }

    public void deleteById(Long id) {
        list.set(id.intValue() - 1, null);
    }

    public void deleteAll() {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, null);
        }
    }
}
