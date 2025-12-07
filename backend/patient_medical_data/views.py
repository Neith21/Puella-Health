from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from .models import PatientMedicalData
from .serializers import PatientMedicalDataSerializer

class PatientMedicalDataViewSet(viewsets.ModelViewSet):
    serializer_class = PatientMedicalDataSerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        queryset = PatientMedicalData.objects.all()

        patient_id = self.request.query_params.get('patient_id')
 
        if patient_id:
            queryset = queryset.filter(patient_id=patient_id)

        return queryset

    def perform_create(self, serializer):
        serializer.save(created_by=self.request.user)

    def perform_update(self, serializer):
        serializer.save(modified_by=self.request.user)