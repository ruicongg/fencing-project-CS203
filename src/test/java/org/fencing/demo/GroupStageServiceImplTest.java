package org.fencing.demo;

    import org.fencing.demo.events.Event;
    import org.fencing.demo.events.EventNotFoundException;
    import org.fencing.demo.events.EventRepository;
    import org.fencing.demo.stages.GroupStage;
    import org.fencing.demo.stages.GroupStageNotFoundException;
    import org.fencing.demo.stages.GroupStageRepository;
    import org.fencing.demo.stages.GroupStageServiceImpl;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.MockitoAnnotations;
    
    import java.util.Optional;
    
    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.doNothing;
    import static org.mockito.Mockito.doThrow;
    import static org.mockito.Mockito.times;
    import static org.mockito.Mockito.verify;
    import static org.mockito.Mockito.when;
        
    public class GroupStageServiceImplTest {
    
        @InjectMocks
        private GroupStageServiceImpl groupStageService;
    
        @Mock
        private GroupStageRepository groupStageRepository;
    
        @Mock
        private EventRepository eventRepository;
    
        @BeforeEach
        public void setUp() {
            MockitoAnnotations.openMocks(this);
        }
    
        @Test
        public void testAddGroupStage() {
            Long eventId = 1L;
            Event event = new Event();
            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
            when(groupStageRepository.save(any(GroupStage.class))).thenReturn(new GroupStage());
    
            GroupStage result = groupStageService.addGroupStage(eventId);
            assertNotNull(result);
        }
    
        @Test
        public void testAddGroupStageEventNotFound() {
            Long eventId = 1L;
            when(eventRepository.findById(eventId)).thenReturn(Optional.empty());
    
            assertThrows(EventNotFoundException.class, () -> {
                groupStageService.addGroupStage(eventId);
            });
        }
    
        @Test
        public void testGetGroupStage() {
            Long groupStageId = 1L;
            GroupStage groupStage = new GroupStage();
            when(groupStageRepository.findById(groupStageId)).thenReturn(Optional.of(groupStage));
    
            GroupStage result = groupStageService.getGroupStage(groupStageId);
            assertNotNull(result);
        }
    
        @Test
        public void testGetGroupStageNotFound() {
            Long groupStageId = 1L;
            when(groupStageRepository.findById(groupStageId)).thenReturn(Optional.empty());
    
            assertThrows(GroupStageNotFoundException.class, () -> {
                groupStageService.getGroupStage(groupStageId);
            });
        }

        // @Test
        // public void testUpdateGroupStage() {
        //     Long eventId = 1L;
        //     Long groupStageId = 1L;
        //     GroupStage newGroupStage = new GroupStage();
        //     GroupStage existingGroupStage = new GroupStage();
        //     Event event = new Event();

        //     existingGroupStage.setEvent(event);
        //     newGroupStage.setEvent(event);

        //     when(groupStageRepository.findById(groupStageId)).thenReturn(Optional.of(existingGroupStage));
        //     when(groupStageRepository.save(existingGroupStage)).thenReturn(existingGroupStage);

        //     GroupStage result = groupStageService.updateGroupStage(eventId, groupStageId, newGroupStage);
        //     assertNotNull(result);
        //     assertEquals(existingGroupStage, result);
        // }

        @Test
        public void testUpdateGroupStageNotFound() {
            Long eventId = 1L;
            Long groupStageId = 1L;
            GroupStage newGroupStage = new GroupStage();

            when(groupStageRepository.findById(groupStageId)).thenReturn(Optional.empty());

            assertThrows(GroupStageNotFoundException.class, () -> {
                groupStageService.updateGroupStage(eventId, groupStageId, newGroupStage);
            });
        }

        // @Test
        // public void testDeleteGroupStage() {
        //     Long eventId = 1L;
        //     Long groupStageId = 1L;

        //     doNothing().when(groupStageRepository).deleteByEventIdAndId(eventId, groupStageId);

        //     groupStageService.deleteGroupStage(eventId, groupStageId);
        //     verify(groupStageRepository, times(1)).deleteByEventIdAndId(eventId, groupStageId);
        // }

        @Test
        public void testDeleteGroupStageNotFound() {
            Long eventId = 1L;
            Long groupStageId = 1L;

            doThrow(new GroupStageNotFoundException(groupStageId)).when(groupStageRepository).deleteByEventIdAndId(eventId, groupStageId);

            assertThrows(GroupStageNotFoundException.class, () -> {
                groupStageService.deleteGroupStage(eventId, groupStageId);
            });
        }

    
    }
    