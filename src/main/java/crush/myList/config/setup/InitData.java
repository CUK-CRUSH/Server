package crush.myList.config.setup;

import crush.myList.config.EnvBean;
import crush.myList.domain.admin.mongo.document.Admin;
import crush.myList.domain.admin.mongo.repository.AdminRepository;
import crush.myList.domain.member.entity.Role;
import crush.myList.domain.member.enums.RoleName;
import crush.myList.domain.member.repository.RoleRepository;
import crush.myList.domain.music.mongo.document.Music;
import crush.myList.domain.music.mongo.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class InitData implements ApplicationListener<ContextRefreshedEvent> {
    private boolean alreadySetup = false;

    private final EnvBean envBean;
    private final RoleRepository roleRepository;
    private final MusicRepository musicRepository;
    private final AdminRepository adminRepository;

    public void createRoleIfNotFound(RoleName roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            Role newRole = Role.builder()
                    .name(roleName)
                    .build();
            roleRepository.save(newRole);
        }
    }

    public void createAdminIfNotFound() {
        if (adminRepository.findByUsername(envBean.getAdminUsername()).isEmpty()) {
            adminRepository.save(Admin.builder()
                    .username(envBean.getAdminUsername())
                    .password(envBean.getAdminPassword())
                    .role(RoleName.ADMIN)
                    .build());
        }
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) { // 애플리케이션 시작 시점에 실행
        // 이미 실행되었다면 종료
        if (alreadySetup)
            return;

        // RoleEnum의 모든 역할 생성
        RoleName[] roleNames = RoleName.values();
        for (RoleName roleName : roleNames) {
            createRoleIfNotFound(roleName);
        }

        // 관리자 계정 생성
        createAdminIfNotFound();

        alreadySetup = true;
    }
}
