package com.university.schedule.utility;

import com.university.schedule.pageable.OffsetBasedPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationSortingUtility {

	public static Pageable getPageable(int limit, int offset, String[] sort) {
		if (sort == null || sort.length < 2) {
			throw new IllegalArgumentException("Sort array must contain at least two elements.");
		}

		String sortField = sort[0];
		String sortDirection = sort[1];

		if (sortField == null || sortDirection == null) {
			throw new IllegalArgumentException("Sort field and direction must be provided.");
		}

		Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort.Order order = new Sort.Order(direction, sortField);

		return OffsetBasedPageRequest.of(limit, offset, Sort.by(order));
	}
}