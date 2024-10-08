package com.prototyne.Users.service.ProductService;

import com.prototyne.apiPayload.code.status.ErrorStatus;
import com.prototyne.apiPayload.exception.handler.TempHandler;
import com.prototyne.Users.converter.ProductConverter;
import com.prototyne.domain.Event;
import com.prototyne.domain.Investment;
import com.prototyne.domain.Product;
import com.prototyne.domain.User;
import com.prototyne.domain.enums.ProductCategory;
import com.prototyne.repository.EventRepository;
import com.prototyne.repository.HeartRepository;
import com.prototyne.repository.InvestmentRepository;
import com.prototyne.repository.UserRepository;
import com.prototyne.config.JwtManager;
import com.prototyne.Users.web.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service("usersEventServiceImpl")
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final InvestmentRepository investmentRepository;
    private final HeartRepository heartRepository;
    private final JwtManager jwtManager;

    @Override
    public ProductDTO.HomeResponse getHomeById(String accessToken) {
        // 유저 아이디 객체 가져옴
        Long userId = jwtManager.validateJwt(accessToken);

        LocalDate now = LocalDate.now();
        List<ProductDTO.EventResponse> poluarList = getEvents(now, "popular")
                .stream().limit(2)
                .map(event -> {
                    Product product = event.getProduct();
                    int investCount = event.getInvestmentList().size();
                    boolean isBookmarked = heartRepository.findFirstByUserIdAndEvent(userId, event).isPresent();
                    return ProductConverter.toEvent(event, product, investCount, isBookmarked);
                })
                .collect(Collectors.toList());

        List<ProductDTO.SearchResponse> imminentList = getEvents(now, "imminent")
                .stream().limit(3)
                .map(event -> {
                    Product product = event.getProduct();
                    int dDay = calculateDDay(now, event.getEventEnd());
                    boolean isBookmarked = heartRepository.findFirstByUserIdAndEvent(userId, event).isPresent();
                    return ProductConverter.toSearch(event, product, dDay, isBookmarked);
                })
                .collect(Collectors.toList());

        List<ProductDTO.SearchResponse> newList = getEvents(now, "new")
                .stream().limit(3)
                .map(event -> {
                    Product product = event.getProduct();
                    int dDay = calculateDDay(now, event.getEventEnd());
                    boolean isBookmarked = heartRepository.findFirstByUserIdAndEvent(userId, event).isPresent();
                    return ProductConverter.toSearch(event, product, dDay, isBookmarked);
                })
                .collect(Collectors.toList());

        return ProductConverter.toHome(poluarList, imminentList, newList);
    }

    @Override
    public List<ProductDTO.EventResponse> getEventsByType(Long userId, String type) {
        LocalDate now = LocalDate.now();

        // 타입별 신청 진행 중인 시제품 이벤트만 가져옴
        List<Event> events = getEvents(now, type);

        // DTO 변환
        return events.stream()
                .map(event -> {
                    Product product = event.getProduct();
                    int investCount = event.getInvestmentList().size();
                    // 북마크 상태 확인
                    boolean isBookmarked = heartRepository.findFirstByUserIdAndEvent(userId, event).isPresent();
                    return ProductConverter.toEvent(event, product, investCount, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO.SearchResponse> getEventsBySearch(String accessToken, String name) {
        Long userId = jwtManager.validateJwt(accessToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당하는 회원이 존재하지 않습니다."));

        addSearchTerm(user, name);


        // 신청 진행 중인 시제품 이벤트만 가져옴
        LocalDate now = LocalDate.now();
        List<Event> events = eventRepository.findAllByProductNameContaining(name).stream()
                .filter(event -> now.isAfter(event.getEventStart()) && now.isBefore(event.getEventEnd()))
                .collect(Collectors.toList());

        return events.stream()
                .map(event -> {
                    Product product = event.getProduct();
                    int dDay = calculateDDay(now, event.getEventEnd());
                    // 북마크 상태 확인
                    boolean isBookmarked = heartRepository.findFirstByUserIdAndEvent(userId, event).isPresent();
                    return ProductConverter.toSearch(event, product, dDay, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    private void addSearchTerm(User user, String searchTerm) {
        List<String> recentSearchList = user.getRecentSearchList();
        recentSearchList.remove(searchTerm);
        recentSearchList.add(0, searchTerm);

        if (recentSearchList.size() > 10) {
            recentSearchList.remove(recentSearchList.size() - 1);
        }
        userRepository.save(user);
    }

    @Override
    public List<String> getRecentSearches(String accessToken) {
        Long userId = jwtManager.validateJwt(accessToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당하는 회원이 존재하지 않습니다."));

        return user.getRecentSearchList();
    }

    @Override
    public List<String> deleteSearchHistory(String searchTerm, String accessToken) {
        Long userId = jwtManager.validateJwt(accessToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당하는 회원이 존재하지 않습니다."));

        user.getRecentSearchList().remove(searchTerm);
        userRepository.save(user);
        return user.getRecentSearchList(); // 업데이트된 최근검색어 목록 10개 반환
    }

    @Override
    public List<String> deleteAllSearchHistory(String accessToken) {
        Long userId = jwtManager.validateJwt(accessToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당하는 회원이 존재하지 않습니다."));

        user.getRecentSearchList().clear();
        userRepository.save(user);
        return user.getRecentSearchList();
    }


    @Override
    public List<ProductDTO.SearchResponse> getEventsByCategory(String accessToken, String category) {
        // 유저 아이디 객체 가져옴
        Long userId = jwtManager.validateJwt(accessToken);
        // 카테고리 타입 변환 (String -> Enum)
        ProductCategory productCategory = changeToProductCategory(category);
        // 신청 진행 중인 시제품 이벤트만 가져옴
        LocalDate now = LocalDate.now();
        List<Event> events = eventRepository.findByProductCategory(productCategory).stream()
                .filter(event -> now.isAfter(event.getEventStart()) && now.isBefore(event.getEventEnd()))
                .collect(Collectors.toList());

        // DTO 변환
        return events.stream()
                .map(event -> {
                    Product product = event.getProduct();
                    int dDay = calculateDDay(now, event.getEventEnd());
                    // 북마크 상태 확인
                    boolean isBookmarked = heartRepository.findFirstByUserIdAndEvent(userId, event).isPresent();
                    return ProductConverter.toSearch(event, product, dDay, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO.EventDetailsResponse getEventDetailsById(String accessToken, Long eventId) {
        // 이벤트 아이디로 이벤트 객체 가져옴
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new TempHandler(ErrorStatus.PRODUCT_ERROR_EVENT));

        // 유저 아이디와 이벤트 아이디로 투자 객체 가져옴
        Long userId = jwtManager.validateJwt(accessToken);
        Investment investment = investmentRepository.findFirstByUserIdAndEventId(userId, eventId)
                .orElse(null);

        Boolean isBookmarked = heartRepository.findFirstByUserIdAndEvent(userId, event).isPresent();


        // DTO로 변환
        return ProductConverter.toEventDetails(event, investment, isBookmarked);
    }

    // 타입별 이벤트 가져옴
    private List<Event> getEvents(LocalDate now, String type) {
        return switch (type) {
            case "popular" -> eventRepository.findAllActiveEventsByPopular(now);
            case "imminent" -> eventRepository.findAllEventsByImminent(now).stream()
                    .filter(event -> event.getEventEnd().isBefore(now.plusDays(3)))
                    .collect(Collectors.toList());
            case "new" -> eventRepository.findAllEventsByNew(now).stream()
                    .filter(event -> event.getEventStart().isAfter(now.minusWeeks(1)))
                    .collect(Collectors.toList());
            default -> throw new TempHandler(ErrorStatus.PRODUCT_ERROR_TYPE);
        };
    }

    // Enum에 category 없으면 오류 처리
    private ProductCategory changeToProductCategory(String category) {
        try {
            return ProductCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new TempHandler(ErrorStatus.PRODUCT_ERROR_CATEGORY);
        }
    }

    // 디데이 계산
    private Integer calculateDDay(LocalDate now, LocalDate endDate) {
        long daysBetween = java.time.Duration.between(now, endDate).toDays();
        return (int) daysBetween;
    }

    public void saveInvestment(Investment investment) {
        investmentRepository.save(investment);
    }
}
