package com.holetbooking.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.holetbooking.HotelBookingApplication;
import com.holetbooking.exception.InvalidBookingRequestException;
import com.holetbooking.exception.ResourseNotFoundException;
import com.holetbooking.model.BookedRoom;
import com.holetbooking.model.Room;
import com.holetbooking.repository.BookingRepository;

@Service
public class BookingServiceImpl implements BookingService {
	
	@Autowired
    private BookingRepository bookingRepository;
	@Autowired
    private RoomService roomService;
    
	@Override
	public void cancelBooking(Long bookingId) {
		bookingRepository.deleteById(bookingId);
		
	}
	@Override
	public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
		return bookingRepository.findByRoomId(roomId);
	}
	@Override
	public String saveBooking(Long roomId, BookedRoom bookingRequest) {
		 if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
	            throw new InvalidBookingRequestException("Check-in date must come before check-out date");
	        }
	        Room room = roomService.getRoomById(roomId).get();
	        List<BookedRoom> existingBookings = room.getBookings();
	        boolean roomIsAvailable = roomIsAvailable(bookingRequest,existingBookings);
	        if (roomIsAvailable){
	            room.addBooking(bookingRequest);
	            bookingRepository.save(bookingRequest);
	        }else{
	            throw  new InvalidBookingRequestException("Sorry, This room is not available for the selected dates;");
	        }
	        return bookingRequest.getBookingConfirmationCode();
	}
	
	@Override
	public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
		 return bookingRepository.findByBookingConfirmationCode(confirmationCode)
	                .orElseThrow(() -> new ResourseNotFoundException("No booking found with booking code :"+confirmationCode));
	}
	@Override
	public List<BookedRoom> getAllBookings() {
		return bookingRepository.findAll();
	}
	@Override
	public List<BookedRoom> getBookingsByUserEmail(String email) {
		return bookingRepository.findByGuestEmail(email);
	}
	
	private boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }


    

}