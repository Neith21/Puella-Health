from rest_framework import serializers
from .models import HealthRecord

class HealthRecordSerializer(serializers.ModelSerializer):

    nombre_paciente = serializers.ReadOnlyField(source='patient.patient_first_name')
    apellido_paciente = serializers.ReadOnlyField(source='patient.patient_last_name')

    class Meta:
        model = HealthRecord
        fields = '__all__'
        read_only_fields = (
            'record_diagnosis',  # <--- La IA llena esto
            'created_by', 
            'modified_by', 
            'created_at', 
            'updated_at',
            'active'
        )