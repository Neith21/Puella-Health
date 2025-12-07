from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from rest_framework.exceptions import APIException
from .models import HealthRecord
from .serializers import HealthRecordSerializer
from utilities.diagnosis_service import ia_agent 

class HealthRecordViewSet(viewsets.ModelViewSet):
    queryset = HealthRecord.objects.all()
    serializer_class = HealthRecordSerializer
    permission_classes = [IsAuthenticated]

    def perform_create(self, serializer):
        user = self.request.user
        
        # 1. Sacamos los datos que vienen del frontend
        medical_data = serializer.validated_data.get('medical_data')
        systolic = serializer.validated_data.get('preassure_systolic')
        diastolic = serializer.validated_data.get('preassure_diastolic')
        
        # 2. Formateamos las presiones para tu prompt
        pressions_str = f"Presión Sistólica: {systolic} / Presión Diastólica: {diastolic}"

        # 3. Llamamos a tu Agente de IA
        # Envolvemos en try/except por si Google se cae o da error
        try:
            diagnosis_result = ia_agent(medical_data, pressions_str)
        except Exception as e:
            # Si la IA falla, podés decidir guardar un error o detener todo.
            # Aquí lanzamos un error 503 para que sepa que falló el servicio externo
            raise APIException(f"Error generando diagnóstico con IA: {e}")

        # 4. Guardamos todo junto
        serializer.save(
            created_by=user,
            record_diagnosis=diagnosis_result # <--- Aquí inyectamos lo que dijo Gemini
        )

    def get_queryset(self):
        queryset = HealthRecord.objects.all()
        
        patient_id = self.request.query_params.get('patient_id')
 
        if patient_id:
            queryset = queryset.filter(patient_id=patient_id)
            
        return queryset

    def perform_update(self, serializer):
        serializer.save(modified_by=self.request.user)