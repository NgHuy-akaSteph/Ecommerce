package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.dto.request.AddressRequest;
import com.myapp.ecommerce.dto.response.AddressResponse;
import com.myapp.ecommerce.entity.Address;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.repository.AddressRepository;
import com.myapp.ecommerce.repository.UserRepository;
import com.myapp.ecommerce.service.AddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AddressServiceImpl implements AddressService {

    AddressRepository addressRepository;
    UserRepository userRepository;

    @Override
    @Transactional
    public AddressResponse create(String userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean wantDefault = Boolean.TRUE.equals(request.getIsDefault());
        boolean isFirst = addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId).isEmpty();

        if (isFirst || wantDefault) {
            // unset other defaults to keep at most 1 default per user
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(prev -> {
                        prev.setDefault(false);
                        addressRepository.save(prev);
                    });
        }

        Address address = Address.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .street(request.getStreet())
                .ward(request.getWard())
                .district(request.getDistrict())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .country(request.getCountry() != null && !request.getCountry().isBlank()
                        ? request.getCountry()
                        : "Vietnam")
                .label(request.getLabel())
                .note(request.getNote())
                .isDefault(isFirst || wantDefault)
                .build();

        return toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse update(String addressId, String userId, AddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        if (request.getFullName() != null) address.setFullName(request.getFullName());
        if (request.getPhone() != null) address.setPhone(request.getPhone());
        if (request.getStreet() != null) address.setStreet(request.getStreet());
        if (request.getWard() != null) address.setWard(request.getWard());
        if (request.getDistrict() != null) address.setDistrict(request.getDistrict());
        if (request.getCity() != null) address.setCity(request.getCity());
        if (request.getPostalCode() != null) address.setPostalCode(request.getPostalCode());
        if (request.getCountry() != null) address.setCountry(request.getCountry());
        if (request.getLabel() != null) address.setLabel(request.getLabel());
        if (request.getNote() != null) address.setNote(request.getNote());

        // Xử lý đổi default
        if (Boolean.TRUE.equals(request.getIsDefault()) && !address.isDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(prev -> {
                        if (!prev.getId().equals(addressId)) {
                            prev.setDefault(false);
                            addressRepository.save(prev);
                        }
                    });
            address.setDefault(true);
        }

        return toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void delete(String addressId, String userId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        boolean wasDefault = address.isDefault();
        addressRepository.delete(address);

        // Nếu xoá địa chỉ mặc định, đặt địa chỉ khác (mới nhất) làm default
        if (wasDefault) {
            List<Address> remaining = addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);
            if (!remaining.isEmpty()) {
                Address newDefault = remaining.get(0);
                newDefault.setDefault(true);
                addressRepository.save(newDefault);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses(String userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getById(String addressId, String userId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        return toResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse setDefault(String addressId, String userId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        if (!address.isDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(prev -> {
                        prev.setDefault(false);
                        addressRepository.save(prev);
                    });
            address.setDefault(true);
            addressRepository.save(address);
        }
        return toResponse(address);
    }

    private AddressResponse toResponse(Address a) {
        return AddressResponse.builder()
                .id(a.getId())
                .userId(a.getUser() != null ? a.getUser().getId() : null)
                .fullName(a.getFullName())
                .phone(a.getPhone())
                .street(a.getStreet())
                .ward(a.getWard())
                .district(a.getDistrict())
                .city(a.getCity())
                .postalCode(a.getPostalCode())
                .country(a.getCountry())
                .label(a.getLabel())
                .note(a.getNote())
                .isDefault(a.isDefault())
                .build();
    }
}
